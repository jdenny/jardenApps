package demo.jdk5;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import demo.jdk5.ToDo.Priority;

@ToDo(description = "add code to display this at run-time",
priority=Priority.MEDIUM, completed=true)
public class MyAnnotation {

	public static void main(String[] args) {
		System.out.println("hello John");
		Class<? extends MyAnnotation> clazz = // MyAnnotation.class;
				A.class;
		Annotation[] annotations = clazz.getAnnotations();
		for (Annotation annotation: annotations) {
			if (annotation instanceof ToDo) {
				ToDo todo = (ToDo) annotation;
				System.out.println("description: " + todo.description());
				System.out.println("priority: " + todo.priority());
				System.out.println("completed: " + todo.completed());
			}
			System.out.println(annotation);
		}
	}
}

class A extends MyAnnotation {
	
}

@Retention(RetentionPolicy.RUNTIME)
@Inherited
@interface ToDo {
	public enum Priority {LOW, MEDIUM, HIGH};
	String description();
	Priority priority() default Priority.LOW;
	boolean completed() default false;
}
