package bachelor.polarity.soPro;

import bachelor.polarity.salsa.corpora.elements.Frame;

import java.util.Collection;

/**
 * @author Erik Hahn
 *
 */
public interface Module {
	/**
	 *
	 * @param sentence
	 * @return A collection of {@link salsa.corpora.elements.Frame} objects. Each
	 *         represents a SubjectiveExpression. Each implementation of this
	 *         interface is expected to implement only a single strategy for
	 *         finding sentiment expressions. Their results are combined by
	 *         {@link SentimentChecker}.
	 */
	Collection<Frame> findFrames(SentenceObj sentence);
}
