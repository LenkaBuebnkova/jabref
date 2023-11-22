package org.jabref.gui.edit;

import java.util.Arrays;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.types.StandardEntryType;
import org.jabref.preferences.BibEntryPreferences;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ManageKeywordsViewModelTest {

    private final BibEntryPreferences bibEntryPreferences = mock(BibEntryPreferences.class);
    private ManageKeywordsViewModel keywordsViewModel;

    @BeforeEach
    void setUp() {
        BibEntry entryOne = new BibEntry(StandardEntryType.Article)
                .withField(StandardField.AUTHOR, "Prakhar Srivastava and Nishant Singh")
                .withField(StandardField.YEAR, "2020")
                .withField(StandardField.DOI, "10.1109/PARC49193.2020.236624")
                .withField(StandardField.ISBN, "978-1-7281-6575-2")
                .withField(StandardField.JOURNALTITLE, "2020 International Conference on Power Electronics & IoT Applications in Renewable Energy and its Control (PARC)")
                .withField(StandardField.PAGES, "351--354")
                .withField(StandardField.PUBLISHER, "IEEE")
                .withField(StandardField.TITLE, "Automatized Medical Chatbot (Medibot)")
                .withField(StandardField.KEYWORDS, "Human-machine interaction, Chatbot, Medical Chatbot, Natural Language Processing, Machine Learning, Bot");

        BibEntry entryTwo = new BibEntry(StandardEntryType.Article)
                .withField(StandardField.AUTHOR, "Mladjan Jovanovic and Marcos Baez and Fabio Casati")
                .withField(StandardField.DATE, "November 2020")
                .withField(StandardField.YEAR, "2020")
                .withField(StandardField.DOI, "10.1109/MIC.2020.3037151")
                .withField(StandardField.ISSN, "1941-0131")
                .withField(StandardField.JOURNALTITLE, "IEEE Internet Computing")
                .withField(StandardField.PAGES, "1--1")
                .withField(StandardField.PUBLISHER, "IEEE")
                .withField(StandardField.TITLE, "Chatbots as conversational healthcare services")
                .withField(StandardField.KEYWORDS, "Chatbot, Medical services, Internet, Data collection, Medical diagnostic imaging, Automation, Vocabulary");

        List<BibEntry> entries = List.of(entryOne, entryTwo);

        when(bibEntryPreferences.getKeywordSeparator()).thenReturn(',');

        keywordsViewModel = new ManageKeywordsViewModel(bibEntryPreferences, entries);
    }

    @Test
    void keywordsFilledInCorrectly() {
        ObservableList<String> addedKeywords = keywordsViewModel.getKeywords();
        List<String> expectedKeywordsList = Arrays.asList("Human-machine interaction", "Chatbot", "Medical Chatbot",
                "Natural Language Processing", "Machine Learning", "Bot", "Chatbot", "Medical services", "Internet",
                "Data collection", "Medical diagnostic imaging", "Automation", "Vocabulary");

        assertEquals(FXCollections.observableList(expectedKeywordsList), addedKeywords);
    }

    @Test
    void removedKeywordNotIncludedInKeywordsList() {
        ObservableList<String> modifiedKeywords = keywordsViewModel.getKeywords();
        List<String> originalKeywordsList = Arrays.asList("Human-machine interaction", "Chatbot", "Medical Chatbot",
                "Natural Language Processing", "Machine Learning", "Bot", "Chatbot", "Medical services", "Internet",
                "Data collection", "Medical diagnostic imaging", "Automation", "Vocabulary");

        assertEquals(FXCollections.observableList(originalKeywordsList), modifiedKeywords, "compared lists are not identical");

        keywordsViewModel.removeKeyword("Human-machine interaction");

        assertNotEquals(FXCollections.observableList(originalKeywordsList), modifiedKeywords, "compared lists are identical");
    }

    @Test
    void getDisplayType() {
        ManageKeywordsDisplayType expectedDisplayType = ManageKeywordsDisplayType.CONTAINED_IN_ALL_ENTRIES;
        assertEquals(expectedDisplayType, keywordsViewModel.getDisplayType(),
                "Initial display type should be CONTAINED_IN_ALL_ENTRIES");
    }

    @Test
    void displayTypeProperty() {
        ObjectProperty<ManageKeywordsDisplayType> displayTypeProperty = keywordsViewModel.displayTypeProperty();
        assertNotNull(displayTypeProperty, "displayTypeProperty should not be null");

        ManageKeywordsDisplayType expectedDisplayType = ManageKeywordsDisplayType.CONTAINED_IN_ANY_ENTRY;
        displayTypeProperty.set(expectedDisplayType);

        assertEquals(expectedDisplayType, displayTypeProperty.get(),
                "displayTypeProperty should return the expected display type");
    }

    @Test
    void saveChangesNoChanges() {
        // No changes should result in no exception or error
        assertDoesNotThrow(() -> keywordsViewModel.saveChanges(),
                "No changes should not throw an exception");
    }

    @Test
    void saveChangesWithChanges() {
        // Modify keywords to simulate changes
        ObservableList<String> modifiedKeywords = keywordsViewModel.getKeywords();
        modifiedKeywords.remove("Human-machine interaction");
        modifiedKeywords.add("NewKeyword");

        // Save changes
        assertDoesNotThrow(() -> keywordsViewModel.saveChanges(),
                "Changes should not throw an exception");
    }
}
