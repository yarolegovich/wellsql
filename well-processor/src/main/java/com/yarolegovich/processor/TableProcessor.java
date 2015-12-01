package com.yarolegovich.processor;


import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.yarolegovich.wellsql.core.Mapper;
import com.yarolegovich.wellsql.core.TableLookup;
import com.yarolegovich.wellsql.core.annotation.Check;
import com.yarolegovich.wellsql.core.annotation.Column;
import com.yarolegovich.wellsql.core.annotation.NotNull;
import com.yarolegovich.wellsql.core.annotation.PrimaryKey;
import com.yarolegovich.wellsql.core.annotation.RawConstraints;
import com.yarolegovich.wellsql.core.annotation.Table;
import com.yarolegovich.wellsql.core.TableClass;
import com.yarolegovich.wellsql.core.annotation.Unique;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class TableProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        Utils.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        TypeName anyClass = CodeGenUtils.wildcard(Class.class);

        FieldSpec tableMap = FieldSpec.builder(ParameterizedTypeName.get(ClassName.get(Map.class),
                        anyClass, ClassName.get(TableClass.class)),
                "tables", Modifier.PRIVATE, Modifier.FINAL).build();

        FieldSpec mapperMap = FieldSpec.builder(CodeGenUtils.mapOfParametrized(Map.class,
                        Class.class, Mapper.class),
                "mappers", Modifier.PRIVATE, Modifier.FINAL).build();

        TypeName concreteTables = ParameterizedTypeName.get(ClassName.get(HashMap.class), anyClass, ClassName.get(TableClass.class));
        TypeName concreteMappers = CodeGenUtils.mapOfParametrized(HashMap.class, Class.class, Mapper.class);
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addStatement("$N = new $T()", tableMap, concreteTables)
                .addStatement("$N = new $T()", mapperMap, concreteMappers);

        boolean generated = false;
        for (Element tableElement : roundEnv.getElementsAnnotatedWith(Table.class)) {
            if (tableElement.getKind() != ElementKind.CLASS) {
                error(tableElement, "Only classes can be annotated with @Table");
                return true;
            }

            try {
                TableAnnotatedClass table = new TableAnnotatedClass(tableElement);
                TypeName token = ClassName.get(tableElement.asType());

                Table annotation = tableElement.getAnnotation(Table.class);

                if (annotation.generateTable()) {
                    String tableName = createTable(tableElement, table);
                    constructorBuilder.addStatement(CodeGenUtils.putForToken(tableName),
                            tableMap, token);
                    generated = true;
                }

                if (annotation.generateMapper()) {
                    String mapperName = createMapper(tableElement, table);
                    constructorBuilder.addStatement(CodeGenUtils.putForToken(mapperName),
                            mapperMap, token);
                    generated = true;
                }
            } catch (TableCreationException e) {
                error(tableElement, "Can't create table class: " + e.getMessage());
            }

        }

        if (generated) {
            generateLookup(constructorBuilder.build(), tableMap, mapperMap);
        }

        return true;
    }

    private String createTable(Element tableElement, TableAnnotatedClass table) {

        String genClassName = table.getTableName() + "Table";
        TypeSpec.Builder tableClassBuilder = TypeSpec.classBuilder(genClassName)
                .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
                .addSuperinterface(TableClass.class);

        MethodSpec createStatement = CodeGenUtils.interfaceMethod("createStatement")
                .returns(String.class)
                .addStatement("return $S", table.toTableDeclaration())
                .build();
        MethodSpec tableName = CodeGenUtils.interfaceMethod("getTableName")
                .returns(String.class)
                .addStatement("return $S", table.getTableName())
                .build();
        TypeName model = ClassName.get(tableElement.asType());
        MethodSpec modelClass = CodeGenUtils.interfaceMethod("getModelClass")
                .returns(CodeGenUtils.wildcard(Class.class))
                .addStatement("return $T.class", model)
                .build();
        MethodSpec isAutoincrement = CodeGenUtils.interfaceMethod("shouldAutoincrementId")
                .returns(boolean.class)
                .addStatement("return " + table.isAutoincrement())
                .build();

        tableClassBuilder.addMethod(createStatement)
                .addMethod(tableName)
                .addMethod(modelClass)
                .addMethod(isAutoincrement);

        for (ColumnAnnotatedField column : table.columns()) {
            tableClassBuilder.addField(columnToStaticConstant(column));
        }

        TypeSpec tableClass = tableClassBuilder.build();
        JavaFile javaFile = JavaFile.builder(CodeGenUtils.PACKAGE, tableClass)
                .build();

        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            error(tableElement, "Failed to create file: " + e.getMessage());
        }

        return CodeGenUtils.PACKAGE + "." + genClassName;
    }

    @SuppressWarnings("unchecked")
    private String createMapper(Element tableElement, TableAnnotatedClass table) {

        TypeName tableType = TypeName.get(tableElement.asType());

        String genClassName = table.getTableName() + "Mapper";
        TypeSpec.Builder mapperClassBuilder = TypeSpec.classBuilder(genClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ParameterizedTypeName.get(ClassName.get(Mapper.class),
                        TypeName.get(tableElement.asType())));

        if (table.hasDate()) {
            TypeName dateFormat = ClassName.get(SimpleDateFormat.class);
            TypeName locale = ClassName.get(Locale.class);
            FieldSpec fField = FieldSpec.builder(dateFormat, CodeGenUtils.FORMATTER, Modifier.PRIVATE)
                    .initializer("new $T($S,$T.getDefault())", dateFormat, "yyyy-MM-dd", locale)
                    .build();
            mapperClassBuilder.addField(fField);
        }

        TypeName map = ParameterizedTypeName.get(Map.class, String.class, Object.class);
        MethodSpec.Builder toCvBuilder = CodeGenUtils.interfaceMethod("toContentValues")
                .addStatement("$T cv = new $T()", map, ParameterizedTypeName.get(HashMap.class, String.class, Object.class))
                .addParameter(ParameterSpec.builder(tableType, "item").build())
                .returns(map);

        MethodSpec.Builder convertBuilder = CodeGenUtils.interfaceMethod("convert")
                .addStatement("$T item = new $T()", tableType, tableType)
                .addParameter(ParameterSpec.builder(map, "cv").build())
                .returns(tableType);

        for (ColumnAnnotatedField column : table.columns()) {

            if (column.isDate()) {
                toCvBuilder.beginControlFlow("if (" + CodeGenUtils.extractValue(column, false) + " != null)");
            }
            toCvBuilder.addStatement(CodeGenUtils.toCvStatement(column), column.getName());
            if (column.isDate()) {
                toCvBuilder.endControlFlow();
            }

            convertBuilder.beginControlFlow(CodeGenUtils.cvGetNotNull(), column.getName());
            convertBuilder.addStatement(CodeGenUtils.toConvertStatement(column), column.getName());
            convertBuilder.endControlFlow();
        }

        toCvBuilder.addStatement("return cv");
        convertBuilder.addStatement("return item");

        mapperClassBuilder.addMethod(toCvBuilder.build());
        mapperClassBuilder.addMethod(convertBuilder.build());

        JavaFile javaFile = JavaFile.builder(CodeGenUtils.PACKAGE, mapperClassBuilder.build())
                .build();
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            error(tableElement, "Failed to create mapper class: " + e.getMessage());
        }

        return CodeGenUtils.PACKAGE + "." + genClassName;
    }

    private void generateLookup(MethodSpec constructor, FieldSpec tableMap, FieldSpec mapperMap) {

        TypeName anyClass = CodeGenUtils.wildcard(Class.class);

        TypeSpec.Builder lookupClassBuilder = TypeSpec.classBuilder(CodeGenUtils.LOOKUP_CLASS)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(TableLookup.class);

        MethodSpec getTableTokens = CodeGenUtils.interfaceMethod("getTableTokens")
                .returns(ParameterizedTypeName.get(ClassName.get(Set.class), anyClass))
                .addStatement("return $N.keySet()", tableMap)
                .build();
        MethodSpec getMapperTokens = CodeGenUtils.interfaceMethod("getMapperTokens")
                .returns(ParameterizedTypeName.get(ClassName.get(Set.class), anyClass))
                .addStatement("return $N.keySet()", mapperMap)
                .build();
        MethodSpec getTable = CodeGenUtils.interfaceMethod("getTable")
                .returns(TableClass.class)
                .addParameter(anyClass, "token")
                .addStatement("return $N.get(token)", tableMap)
                .build();
        TypeVariableName t = TypeVariableName.get("T");
        ParameterizedTypeName parametrizedMapper = ParameterizedTypeName.get(ClassName.get(Mapper.class), t);
        ParameterizedTypeName parametrizedClass = ParameterizedTypeName.get(ClassName.get(Class.class), t);
        MethodSpec getMapper = CodeGenUtils.interfaceMethod("getMapper")
                .addTypeVariable(t)
                .returns(parametrizedMapper)
                .addParameter(ParameterSpec.builder(parametrizedClass, "token").build())
                .addStatement("return ($T) $N.get(token)", parametrizedMapper, mapperMap)
                .build();

        TypeSpec lookup = lookupClassBuilder.addField(tableMap).addField(mapperMap)
                .addMethod(constructor)
                .addMethod(getTableTokens)
                .addMethod(getMapperTokens)
                .addMethod(getTable)
                .addMethod(getMapper)
                .build();

        JavaFile javaFile = JavaFile.builder(CodeGenUtils.PACKAGE, lookup).build();
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FieldSpec columnToStaticConstant(ColumnAnnotatedField column) {
        String name = column.getName()
                .replaceAll("^_", "")
                .toUpperCase();
        return FieldSpec.builder(String.class, name)
                .addModifiers(Modifier.STATIC, Modifier.FINAL, Modifier.PUBLIC)
                .initializer("$S", column.getName())
                .build();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedTypes = new HashSet<>();
        supportedTypes.add(Check.class.getCanonicalName());
        supportedTypes.add(NotNull.class.getCanonicalName());
        supportedTypes.add(PrimaryKey.class.getCanonicalName());
        supportedTypes.add(RawConstraints.class.getCanonicalName());
        supportedTypes.add(Unique.class.getCanonicalName());
        supportedTypes.add(Column.class.getCanonicalName());
        supportedTypes.add(Table.class.getCanonicalName());
        return supportedTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    public void error(Element e, String error) {
        messager.printMessage(Diagnostic.Kind.ERROR, error, e);
    }

    public void warning(String message) {
        messager.printMessage(Diagnostic.Kind.WARNING, message);
    }
}
