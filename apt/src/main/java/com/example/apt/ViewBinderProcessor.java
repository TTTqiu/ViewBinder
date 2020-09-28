package com.example.apt;

import com.example.annotation.Bind;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.example.annotation.Bind")
//@SupportedOptions("")
public class ViewBinderProcessor extends AbstractProcessor {

    private Messager mMessager;
    private Filer mFiler;

    // Activities
    private  Map<TypeElement, Set<VariableElement>> mTypeElements = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mMessager = processingEnvironment.getMessager();
        mFiler = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<VariableElement> elements = ElementFilter.fieldsIn(roundEnvironment.getElementsAnnotatedWith(Bind.class));
        for (VariableElement element : elements) {
            TypeElement typeElement = (TypeElement)element.getEnclosingElement();
            Set<VariableElement> variableElements =mTypeElements.get(typeElement);
            if (variableElements == null) {
                Set<VariableElement> newSet = new HashSet<>();
                newSet.add(element);
                mTypeElements.put(typeElement, newSet);
            } else {
                variableElements.add(element);
            }
        }

        for (TypeElement typeElement  : mTypeElements.keySet()) {
            buildJava(typeElement);
        }

        return false;
    }

    private void buildJava(TypeElement typeElement) {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("bind")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addParameter(ClassName.get(typeElement),"activity");
        for (VariableElement  variableElement : mTypeElements.get(typeElement)) {
            methodBuilder.addStatement("activity.$S = ($T)findViewById($L)",
                    variableElement.getSimpleName(), variableElement.asType(),
                    variableElement.getAnnotation(Bind.class).value());
        }
        TypeSpec typeSpec = TypeSpec.classBuilder(typeElement.getSimpleName() + "_binding")
                .addModifiers(Modifier.PUBLIC,Modifier.FINAL)
                .addMethod(methodBuilder.build())
                .build();
        JavaFile file = JavaFile.builder(ClassName.get(typeElement).packageName(),typeSpec).build();
        try {
            file.writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
            log(e.getMessage());
        }
    }

    private void log(String log) {
        mMessager.printMessage(Diagnostic.Kind.NOTE, log);
        System.out.println(log);
    }
}