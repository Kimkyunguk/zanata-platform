package org.zanata.feature.editor;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    EditorFilterMessagesTest.class,
    TranslateTextTest.class,
    TranslationHistoryTest.class
})
public class EditorTestSuite {
}
