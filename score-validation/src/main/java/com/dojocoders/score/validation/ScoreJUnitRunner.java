package com.dojocoders.score.validation;

import java.util.List;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import com.dojocoders.score.validation.annotations.Persist;
import com.dojocoders.score.validation.config.PersistenceConfiguration;
import com.dojocoders.score.validation.persistence.DisabledScorePersistenceUnit;
import com.dojocoders.score.validation.persistence.ScorePersistenceUnit;
import com.google.common.base.Throwables;

public class ScoreJUnitRunner extends BlockJUnit4ClassRunner {

	private static final Class<DisabledScorePersistenceUnit> UNDEFINED_PERSISTENCE_CLASS = DisabledScorePersistenceUnit.class;

	private PersistenceListener persistenceListener;

	public ScoreJUnitRunner(Class<?> klass) throws InitializationError {
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
			Class<? extends ScorePersistenceUnit> persistenceUnitClass = annotation != null ? annotation.value() : UNDEFINED_PERSISTENCE_CLASS;
			persistenceListener = new PersistenceListener(persistenceUnitClass.newInstance(), new PersistenceConfiguration());
		} catch (ReflectiveOperationException e) {
			Throwables.propagate(e);
		}
	}

	@Override
	protected void collectInitializationErrors(List<Throwable> errors) {
		super.collectInitializationErrors(errors);
		initPersistenceListener();
		persistResultOnInstantiationError(errors);
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

}
