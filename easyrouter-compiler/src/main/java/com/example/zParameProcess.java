package com.example;

import com.example.entity.ParameAnnotationClass;
import com.example.entity.ParameAnnotationGenerator;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

/**
 * 参数自动注入的APT
 * Created by Zane on 2016/12/28.
 * Email: zanebot96@gmail.com
 * Blog: zane96.github.io
 */

@AutoService(Processor.class)
public class zParameProcess extends BaseProcess{

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        ParameAnnotationGenerator.getInstance().clear();

        for (Element parameElement : roundEnv.getElementsAnnotatedWith(Param.class)){
            if (parameElement.getKind() != ElementKind.FIELD){
                printError(parameElement, "Only field can be annotated with @Param");
                return true;
            }
            ParameAnnotationClass parameAnnotationClass = new ParameAnnotationClass(parameElement);
            ParameAnnotationGenerator.getInstance().put(getClassName(parameElement), parameAnnotationClass);
        }
        try {
            ParameAnnotationGenerator.getInstance().generateCode(elementUtils, filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    //获取Activity或者Fragment的类名
    private ClassName getClassName(Element element){
        //获取class type
        TypeElement classElement = (TypeElement) element.getEnclosingElement();
        return ClassName.bestGuess(classElement.getQualifiedName().toString());
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> anntations = new LinkedHashSet<>();
        anntations.add(Param.class.getCanonicalName());
        return anntations;
    }
}
