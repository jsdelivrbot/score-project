package com.dojocoders.score.junit;

import com.dojocoders.score.junit.annotations.InjectImpl;
import com.dojocoders.score.junit.annotations.Persist;
import com.dojocoders.score.junit.persistence.TestPersistUnit;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.InitializationError;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ScoreBlockJUnit4ClassRunner extends BlockJUnit4ClassRunner {

	private static final Reflections REFLECT_UTIL = new Reflections(ClasspathHelper.forJavaClassPath(), TestConfiguration.getImplementationSubpackage());

	private static final Map<Class<?>, Class<?>> IMPLEMENTATIONS = Maps.newConcurrentMap();

	private PersistenceListener persistenceListener;

	public ScoreBlockJUnit4ClassRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	public void run(RunNotifier notifier) {
		notifier.addListener(persistenceListener);
		super.run(notifier);
	}

	private void initPersistenceListener() {
		try {
			Persist annotation = this.getTestClass().getAnnotation(Persist.class);
			Class<? extends TestPersistUnit> persistenceUnitClass = annotation == null ? TestConfiguration.DEFAULT_TEST_PERSISTENCE_CLASS : annotation.value();
			persistenceListener = new PersistenceListener(persistenceUnitClass.newInstance());
		} catch (ReflectiveOperationException e) {
			Throwables.propagate(e);
		}
	}

	@Override
	protected void collectInitializationErrors(List<Throwable> errors) {
		super.collectInitializationErrors(errors);
		this.validateImplementationInjection(errors);
		initPersistenceListener();
		persistResultOnInstantiationError(errors);
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
		Set<Class<? extends T>> classes = REFLECT_UTIL.getSubTypesOf(type);

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

	private void persistResultOnInstantiationError(List<Throwable> errors) {
		if (!errors.isEmpty()) {
			try {
				persistenceListener.testRunFinished(null);
			} catch (Exception e) {
				Throwables.propagate(e);
			}
		}
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
