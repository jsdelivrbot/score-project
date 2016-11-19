package org.soneira.score.junit;

import org.soneira.score.junit.annotations.InjectImpl;
import org.soneira.score.junit.annotations.Persist;
import org.soneira.score.junit.persistence.PersistUnit;
import org.soneira.score.junit.persistence.StaticMap;

import com.couchbase.client.java.search.queries.NumericRangeQuery;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.ClassPath;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.InitializationError;
import org.reflections.Reflections;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

public class ScoreBlockJUnit4ClassRunner extends BlockJUnit4ClassRunner {

	private static final Reflections reflections = new Reflections(System.getProperty("impl.subpackage"));

	private static final Map<Class<?>, Class<?>> IMPLEMENTATIONS = Maps.newConcurrentMap();
	
	public ScoreBlockJUnit4ClassRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	public void run(RunNotifier notifier) {
		addPersistenceListener(notifier);
		super.run(notifier);
	}

	private void addPersistenceListener(RunNotifier notifier) {
		try {
			Persist annotation = this.getTestClass().getAnnotation(Persist.class);
			Class<? extends PersistUnit> persistenceUnitClass = annotation == null ? StaticMap.class : annotation.value();
			notifier.addListener(new PersistenceListener(persistenceUnitClass.newInstance()));
		} catch (ReflectiveOperationException e) {
			Throwables.propagate(e);
		}
	}

	@Override
	protected void collectInitializationErrors(List<Throwable> errors) {
		super.collectInitializationErrors(errors);
		this.validateImplementationInjection(errors);
	}

	protected void validateImplementationInjection(List<Throwable> errors) {
		List<FrameworkField> fields = this.getTestClass().getAnnotatedFields(InjectImpl.class);
		for (FrameworkField field : fields) {
			if(!IMPLEMENTATIONS.containsKey(field.getType())) {
				validateInjectionOfField(errors, field);
			}
		}
	}

	private void validateInjectionOfField(List<Throwable> errors, FrameworkField field) {
		try {
			Class<?> implementation = findImplementation(field.getType());
			IMPLEMENTATIONS.put(field.getType(), implementation);
		} catch(ReflectiveOperationException e) {
			errors.add(e);
		}
	}

	private <T> Class<?> findImplementation(Class<T> type) throws ReflectiveOperationException {
		Set<Class<? extends T>> classes = reflections.getSubTypesOf(type);

		if (classes.isEmpty()) {
			String gripe = InjectImpl.class.getSimpleName() + " annotated field " + type.getSimpleName()
					+ " : IMPLEMENTATION NOT FOUND";
			throw new ClassNotFoundException(gripe);
		}

		if (classes.size() > 1) {
			String gripe = InjectImpl.class.getSimpleName() + " annotated field " + type.getSimpleName()
					+ " : MULTIPLES IMPLEMENTATIONS FOUND";
			throw new ClassNotFoundException(gripe);
		}

		Class<?> implementationClass = classes.iterator().next();
		if (!containsOnlyOneConstructorWithoutArguments(implementationClass)) {
			String gripe = InjectImpl.class.getSimpleName() + " annotated field " + type.getSimpleName() + " : Implementation "
					+ implementationClass.getSimpleName() + " MUST CONTAINS ONLY ONE CONSTRUCTOR WITHOUT ARGUMENTS";
			throw new NoSuchMethodException(gripe);
		}

		return implementationClass;
	}

	private boolean containsOnlyOneConstructorWithoutArguments(Class<?> injImplClass) {
		Constructor<?>[] constructors = injImplClass.getConstructors();
		return constructors.length == 1 && constructors[0].getTypeParameters().length == 0;
	}

	@Override
	protected Object createTest() throws Exception {
		Object testClass = super.createTest();
		injectImplementations(testClass);
		return testClass;
	}

	private void injectImplementations(Object testClass) throws Exception {
		List<FrameworkField> fields = this.getTestClass().getAnnotatedFields(InjectImpl.class);
		for (FrameworkField field : fields) {
			Class<?> clazz = IMPLEMENTATIONS.get(field.getType());
			Field fieldOnInstance = testClass.getClass().getDeclaredField(field.getName());
			fieldOnInstance.setAccessible(true);
			fieldOnInstance.set(testClass, clazz.newInstance());
		}
	}

}
