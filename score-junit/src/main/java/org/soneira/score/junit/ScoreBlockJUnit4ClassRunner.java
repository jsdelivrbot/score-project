package org.soneira.score.junit;

import org.soneira.score.junit.annotations.InjectImpl;
import org.soneira.score.junit.annotations.Persist;
import org.soneira.score.junit.persistence.PersistUnit;
import org.soneira.score.junit.persistence.StaticMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.ClassPath;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.InitializationError;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ScoreBlockJUnit4ClassRunner extends BlockJUnit4ClassRunner {

	InjectImplLoader injectImplLoader;

	public ScoreBlockJUnit4ClassRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	public void run(RunNotifier notifier) {
		try {

			Persist annotation = this.getTestClass().getAnnotation(Persist.class);
			Class<? extends PersistUnit> persistenceUnitClass = annotation == null ? StaticMap.class : annotation.value();
			notifier.addListener(new PersistenceListener(persistenceUnitClass.newInstance()));
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		super.run(notifier);
	}

	@Override
	protected void collectInitializationErrors(List<Throwable> errors) {
		injectImplLoader = new InjectImplLoader(this.getClass().getClassLoader(), System.getProperty("impl.subpackage"));
		super.collectInitializationErrors(errors);
		this.validateInjectImplOnFields(errors);
	}

	private void validateInjectImplOnFields(List<Throwable> errors) {
		List<FrameworkField> fields = this.getTestClass().getAnnotatedFields(InjectImpl.class);
		for (FrameworkField field : fields) {
			List<Class<?>> classes = injectImplLoader.getImplClasses(field.getType());

			if (classes.isEmpty()) {
				String gripe = InjectImpl.class.getSimpleName() + " annotated field " + field.getType().getSimpleName()
						+ " : NOT IMPLEMENTATION FOUND";
				errors.add(new Exception(gripe));
				return;
			}

			if (classes.size() > 1) {
				String gripe = InjectImpl.class.getSimpleName() + " annotated field " + field.getType().getSimpleName()
						+ " : MULTIPLES IMPLEMENTATIONS FOUND";
				errors.add(new Exception(gripe));
				return;
			}

			if (!containsOnlyOneConstructorWithoutArguments(classes.get(0))) {
				String gripe = InjectImpl.class.getSimpleName() + " annotated field " + field.getType().getSimpleName() + " : Implementation "
						+ classes.get(0).getSimpleName() + " MUST CONTAINS ONLY ONE CONSTRUCTOR WITHOUT ARGUMENTS";
				errors.add(new Exception(gripe));
				return;
			}
		}

	}

	private boolean containsOnlyOneConstructorWithoutArguments(Class<?> injImplClass) {
		Constructor<?>[] constructors = injImplClass.getConstructors();
		return constructors.length == 1 && constructors[0].getTypeParameters().length == 0;
	}

	@Override
	protected Object createTest() throws Exception {
		Object testClass = super.createTest();
		injectImplImpl(testClass);
		return testClass;
	}

	private void injectImplImpl(Object testClass) throws Exception {
		List<FrameworkField> fields = this.getTestClass().getAnnotatedFields(InjectImpl.class);
		for (FrameworkField field : fields) {
			Class<?> clazz = injectImplLoader.getImplClasses(field.getType()).get(0);
			Field fieldOnInstance = testClass.getClass().getDeclaredField(field.getName());
			fieldOnInstance.setAccessible(true);
			fieldOnInstance.set(testClass, clazz.newInstance());
		}
	}

	private class InjectImplLoader {
		private final ClassLoader classLoader;
		private final String subpackage;
		private Map<Class<?>, List<Class<?>>> cache = Maps.newConcurrentMap();

		public InjectImplLoader(ClassLoader classLoader, String subpackage) {
			this.classLoader = classLoader;
			this.subpackage = subpackage;
		}

		protected List<Class<?>> getImplClasses(Class<?> fieldType) {
			if (cache.containsKey(fieldType)) {
				return cache.get(fieldType);
			}
			List<Class<?>> classes = Lists.newArrayList();
			for (ClassPath.ClassInfo classInfo : getClasses()) {

				try {
					Class<?> classLoaded = classInfo.load();
					if (classLoaded != null && !classLoaded.equals(Object.class) && !classLoaded.isInterface()
							&& fieldType.isAssignableFrom(classLoaded)) {
						classes.add(classLoaded);
					}
				} catch (Throwable t) {
					// IGNORE
				}

			}
			cache.put(fieldType, classes);
			return classes;
		}

		private Set<ClassPath.ClassInfo> getClasses() {
			try {
				ClassPath classpath = ClassPath.from(classLoader);
				return subpackage != null ? classpath.getTopLevelClassesRecursive(subpackage) :  classpath.getAllClasses();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
