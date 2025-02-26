package org.odk.collect.android.views;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.flexbox.FlexboxLayoutManager;

import org.javarosa.core.model.SelectChoice;
import org.javarosa.core.model.data.helper.Selection;
import org.javarosa.form.api.FormEntryPrompt;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.odk.collect.android.adapters.AbstractSelectListAdapter;
import org.odk.collect.android.adapters.SelectMultipleListAdapter;
import org.odk.collect.android.adapters.SelectOneListAdapter;
import org.odk.collect.android.audio.AudioButton;
import org.odk.collect.android.audio.AudioHelper;
import org.odk.collect.android.formentry.questions.AudioVideoImageTextLabel;
import org.odk.collect.android.listeners.SelectItemClickListener;
import org.odk.collect.android.support.MockFormEntryPromptBuilder;
import org.odk.collect.android.support.RobolectricHelpers;
import org.odk.collect.android.support.TestScreenContextActivity;
import org.odk.collect.android.utilities.WidgetAppearanceUtils;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.odk.collect.android.support.RobolectricHelpers.populateRecyclerView;

@RunWith(RobolectricTestRunner.class)
public class ChoicesRecyclerViewTest {
    private ChoicesRecyclerView recyclerView;

    private ActivityController<TestScreenContextActivity> activityController;

    private FormEntryPrompt formEntryPrompt;

    @Before
    public void setUp() {
        activityController = RobolectricHelpers.buildThemedActivity(TestScreenContextActivity.class);
        Activity activity = activityController.get();
        FrameLayout frameLayout = new FrameLayout(activity);
        activity.setContentView(frameLayout);
        activityController.create().start().visible();
        recyclerView = new ChoicesRecyclerView(activity);
        frameLayout.addView(recyclerView);
        populateRecyclerView(recyclerView);
    }

    @Test
    public void whenNonFLexAppearanceIsUsed_shouldGridLayoutManagerBeUsed() {
        SelectOneListAdapter adapter = new SelectOneListAdapter(null, null, null, new ArrayList<>(), null, null, null, 0, 1, false);
        recyclerView.initRecyclerView(adapter, false);
        assertThat(recyclerView.getLayoutManager().getClass().getName(), equalTo(GridLayoutManager.class.getName()));
    }

    @Test
    public void whenFLexAppearanceIsUsed_shouldFlexboxLayoutManagerBeUsed() {
        SelectOneListAdapter adapter = new SelectOneListAdapter(null, null, null, new ArrayList<>(), null, null, null, 0, 1, false);
        recyclerView.initRecyclerView(adapter, true);
        assertThat(recyclerView.getLayoutManager().getClass().getName(), equalTo(FlexboxLayoutManager.class.getName()));
    }

    @Test
    public void whenNonFLexAppearanceIsUsedWithOneColumn_shouldDividersBeAdded() {
        List<SelectChoice> items = getTestChoices();
        setUpFormEntryPrompt(items, "");

        SelectOneListAdapter adapter = new SelectOneListAdapter(null, null, activityController.get(), items, formEntryPrompt, null, null, 0, 1, false);

        recyclerView.initRecyclerView(adapter, false);

        assertThat(recyclerView.getItemDecorationCount(), is(1));
        assertThat(recyclerView.getItemDecorationAt(0), is(instanceOf(DividerItemDecoration.class)));
    }

    @Test
    public void whenNonFLexAppearanceIsUsedWithMoreThanOneColumn_shouldNotDividersBeAdded() {
        List<SelectChoice> items = getTestChoices();
        setUpFormEntryPrompt(items, "");

        SelectOneListAdapter adapter = new SelectOneListAdapter(null, null, activityController.get(), items, formEntryPrompt, null, null, 0, 2, false);

        recyclerView.initRecyclerView(adapter, false);

        assertThat(recyclerView.getItemDecorationCount(), is(0));
    }

    @Test
    public void whenFLexAppearanceIsUsed_shouldNotDividersBeAdded() {
        List<SelectChoice> items = getTestChoices();
        setUpFormEntryPrompt(items, "");

        SelectOneListAdapter adapter = new SelectOneListAdapter(null, null, activityController.get(), items, formEntryPrompt, null, null, 0, 2, false);

        recyclerView.initRecyclerView(adapter, true);

        assertThat(recyclerView.getItemDecorationCount(), is(0));
    }

    @Test
    public void whenChoicesFiltered_shouldProperValuesBeReturnedInSelectOneButtonsMode() {
        List<SelectChoice> items = getTestChoices();
        setUpFormEntryPrompt(items, "");

        SelectOneListAdapter adapter = new SelectOneListAdapter(null, null, activityController.get(), items, formEntryPrompt, null, null, 0, 1, false);

        recyclerView.initRecyclerView(adapter, false);

        assertVisibleItemsInButtonsMode("AAA", "BBB");
        adapter.getFilter().filter("b");
        assertVisibleItemsInButtonsMode("BBB");
        adapter.getFilter().filter("bc");
        assertVisibleItemsInButtonsMode();
        adapter.getFilter().filter("b");
        assertVisibleItemsInButtonsMode("BBB");
        adapter.getFilter().filter("");
        assertVisibleItemsInButtonsMode("AAA", "BBB");
    }

    @Test
    public void whenChoicesFiltered_shouldProperValuesBeReturnedInSelectMultiButtonsMode() {
        List<SelectChoice> items = getTestChoices();
        setUpFormEntryPrompt(items, "");

        SelectMultipleListAdapter adapter = new SelectMultipleListAdapter(new ArrayList<>(), null, activityController.get(), items, formEntryPrompt, null, null, 0, 1, false);

        recyclerView.initRecyclerView(adapter, false);

        assertVisibleItemsInButtonsMode("AAA", "BBB");
        adapter.getFilter().filter("b");
        assertVisibleItemsInButtonsMode("BBB");
        adapter.getFilter().filter("bc");
        assertVisibleItemsInButtonsMode();
        adapter.getFilter().filter("b");
        assertVisibleItemsInButtonsMode("BBB");
        adapter.getFilter().filter("");
        assertVisibleItemsInButtonsMode("AAA", "BBB");
    }

    @Test
    public void whenChoicesFiltered_shouldProperValuesBeReturnedInSelectOneNoButtonsMode() {
        List<SelectChoice> items = getTestChoices();
        setUpFormEntryPrompt(items, "no-buttons");

        SelectOneListAdapter adapter = new SelectOneListAdapter(null, null, activityController.get(), items, formEntryPrompt, null, null, 0, 1, true);

        recyclerView.initRecyclerView(adapter, false);

        assertVisibleItemsInNoButtonsMode("AAA", "BBB");
        adapter.getFilter().filter("b");
        assertVisibleItemsInNoButtonsMode("BBB");
        adapter.getFilter().filter("bc");
        assertVisibleItemsInNoButtonsMode();
        adapter.getFilter().filter("b");
        assertVisibleItemsInNoButtonsMode("BBB");
        adapter.getFilter().filter("");
        assertVisibleItemsInNoButtonsMode("AAA", "BBB");
    }

    @Test
    public void whenChoicesFiltered_shouldProperValuesBeReturnedInSelectMultiNoButtonsMode() {
        List<SelectChoice> items = getTestChoices();
        setUpFormEntryPrompt(items, "no-buttons");

        SelectMultipleListAdapter adapter = new SelectMultipleListAdapter(new ArrayList<>(), null, activityController.get(), items, formEntryPrompt, null, null, 0, 1, true);

        recyclerView.initRecyclerView(adapter, false);

        assertVisibleItemsInNoButtonsMode("AAA", "BBB");
        adapter.getFilter().filter("b");
        assertVisibleItemsInNoButtonsMode("BBB");
        adapter.getFilter().filter("bc");
        assertVisibleItemsInNoButtonsMode();
        adapter.getFilter().filter("b");
        assertVisibleItemsInNoButtonsMode("BBB");
        adapter.getFilter().filter("");
        assertVisibleItemsInNoButtonsMode("AAA", "BBB");
    }

    @Test
    public void whenClickOneOption_shouldPreviouslySelectedOptionBeUnselectedInSelectOneButtonsMode() {
        List<SelectChoice> items = getTestChoices();
        setUpFormEntryPrompt(items, "");

        SelectItemClickListener listener = mock(SelectItemClickListener.class);
        SelectOneListAdapter adapter = new SelectOneListAdapter(null, listener, activityController.get(), items, formEntryPrompt, null, null, 0, 1, false);

        recyclerView.initRecyclerView(adapter, false);

        clickChoice(0); // Select AAA
        assertThat(isItemSelected(0), is(true));
        assertThat(isItemSelected(1), is(false));

        clickChoice(1); // Select BBB
        assertThat(isItemSelected(0), is(false));
        assertThat(isItemSelected(1), is(true));
    }

    @Test
    public void whenClickOneOption_shouldPreviouslySelectedOptionRemainSelectedInSelectMultiButtonsMode() {
        List<SelectChoice> items = getTestChoices();
        setUpFormEntryPrompt(items, "");

        SelectItemClickListener listener = mock(SelectItemClickListener.class);
        SelectMultipleListAdapter adapter = new SelectMultipleListAdapter(new ArrayList<>(), listener, activityController.get(), items, formEntryPrompt, null, null, 0, 1, false);

        recyclerView.initRecyclerView(adapter, false);

        clickChoice(0); // Select AAA
        assertThat(isItemSelected(0), is(true));
        assertThat(isItemSelected(1), is(false));

        clickChoice(1); // Select BBB
        assertThat(isItemSelected(0), is(true));
        assertThat(isItemSelected(1), is(true));
    }

    @Test
    public void whenClickOneOption_shouldPreviouslySelectedOptionBeUnselectedInSelectOneNoButtonsMode() {
        List<SelectChoice> items = getTestChoices();
        setUpFormEntryPrompt(items, "no-buttons");

        SelectItemClickListener listener = mock(SelectItemClickListener.class);
        SelectOneListAdapter adapter = new SelectOneListAdapter(null, listener, activityController.get(), items, formEntryPrompt, null, null, 0, 1, true);

        recyclerView.initRecyclerView(adapter, false);

        clickChoice(0); // Select AAA
        assertThat(isItemSelected(0), is(true));
        assertThat(isItemSelected(1), is(false));

        clickChoice(1); // Select BBB
        assertThat(isItemSelected(0), is(false));
        assertThat(isItemSelected(1), is(true));
    }

    @Test
    public void whenClickOneOption_shouldPreviouslySelectedOptionRemainSelectedInSelectMultiNoButtonsMode() {
        List<SelectChoice> items = getTestChoices();
        setUpFormEntryPrompt(items, "no-buttons");

        SelectItemClickListener listener = mock(SelectItemClickListener.class);
        SelectMultipleListAdapter adapter = new SelectMultipleListAdapter(new ArrayList<>(), listener, activityController.get(), items, formEntryPrompt, null, null, 0, 1, true);

        recyclerView.initRecyclerView(adapter, false);

        clickChoice(0); // Select AAA
        assertThat(isItemSelected(0), is(true));
        assertThat(isItemSelected(1), is(false));

        clickChoice(1); // Select BBB
        assertThat(isItemSelected(0), is(true));
        assertThat(isItemSelected(1), is(true));
    }

    @Test
    public void whenClickOneElementTwice_shouldThatElementRemainSelectedInSelectOneButtonsMode() {
        List<SelectChoice> items = getTestChoices();
        setUpFormEntryPrompt(items, "");

        SelectItemClickListener listener = mock(SelectItemClickListener.class);
        SelectOneListAdapter adapter = new SelectOneListAdapter(null, listener, activityController.get(), items, formEntryPrompt, null, null, 0, 1, false);

        recyclerView.initRecyclerView(adapter, false);

        clickChoice(0); // Select AAA
        assertThat(isItemSelected(0), is(true));
        assertThat(isItemSelected(1), is(false));

        clickChoice(0); // Select AAA again
        assertThat(isItemSelected(0), is(true));
        assertThat(isItemSelected(1), is(false));
    }

    @Test
    public void whenClickOneElementTwice_shouldThatElementBeUnselectedInSelectMultiButtonsMode() {
        List<SelectChoice> items = getTestChoices();
        setUpFormEntryPrompt(items, "");

        SelectItemClickListener listener = mock(SelectItemClickListener.class);
        SelectMultipleListAdapter adapter = new SelectMultipleListAdapter(new ArrayList<>(), listener, activityController.get(), items, formEntryPrompt, null, null, 0, 1, false);

        recyclerView.initRecyclerView(adapter, false);

        clickChoice(0); // Select AAA
        assertThat(isItemSelected(0), is(true));
        assertThat(isItemSelected(1), is(false));

        clickChoice(0); // Select AAA again
        assertThat(isItemSelected(0), is(false));
        assertThat(isItemSelected(1), is(false));
    }

    @Test
    public void whenClickOneElementTwice_shouldThatElementRemainSelectedInSelectOneNoButtonsMode() {
        List<SelectChoice> items = getTestChoices();
        setUpFormEntryPrompt(items, "no-buttons");

        SelectItemClickListener listener = mock(SelectItemClickListener.class);
        SelectOneListAdapter adapter = new SelectOneListAdapter(null, listener, activityController.get(), items, formEntryPrompt, null, null, 0, 1, true);

        recyclerView.initRecyclerView(adapter, false);

        clickChoice(0); // Select AAA
        assertThat(isItemSelected(0), is(true));
        assertThat(isItemSelected(1), is(false));

        clickChoice(0); // Select AAA again
        assertThat(isItemSelected(0), is(true));
        assertThat(isItemSelected(1), is(false));
    }

    @Test
    public void whenClickOneElementTwice_shouldThatElementBeUnselectedInSelectMultiNoButtonsMode() {
        List<SelectChoice> items = getTestChoices();
        setUpFormEntryPrompt(items, "no-buttons");

        SelectItemClickListener listener = mock(SelectItemClickListener.class);
        SelectMultipleListAdapter adapter = new SelectMultipleListAdapter(new ArrayList<>(), listener, activityController.get(), items, formEntryPrompt, null, null, 0, 1, true);

        recyclerView.initRecyclerView(adapter, false);

        clickChoice(0); // Select AAA
        assertThat(isItemSelected(0), is(true));
        assertThat(isItemSelected(1), is(false));

        clickChoice(0); // Select AAA again
        assertThat(isItemSelected(0), is(false));
        assertThat(isItemSelected(1), is(false));
    }

    @Test
    public void whenButtonsModeIsUsed_shouldViewAndItsElementsBeLongClickableToSupportRemovingAnswers() {
        List<SelectChoice> items = getTestChoices();
        setUpFormEntryPrompt(items, "");

        SelectItemClickListener listener = mock(SelectItemClickListener.class);
        SelectMultipleListAdapter adapter = new SelectMultipleListAdapter(new ArrayList<>(), listener, activityController.get(), items, formEntryPrompt, null, null, 0, 1, false);

        recyclerView.initRecyclerView(adapter, false);

        AudioVideoImageTextLabel view = (AudioVideoImageTextLabel) getChoiceView(0);
        File file = mock(File.class);
        when(file.exists()).thenReturn(true);
        view.setImage(file);
        view.setVideo(file);
        AudioHelper audioHelper = mock(AudioHelper.class);
        MutableLiveData<Boolean> isPlaying = new MutableLiveData<>(false);
        when(audioHelper.setAudio(any(AudioButton.class), any())).thenReturn(isPlaying);
        view.setAudio("file://audio.mp3", audioHelper);

        assertThat(view.isLongClickable(), is(true));
        assertThat(view.getImageView().isLongClickable(), is(true));
        assertThat(view.getVideoButton().isLongClickable(), is(true));
        assertThat(view.getAudioButton().isLongClickable(), is(true));
        assertThat(view.getLabelTextView().isLongClickable(), is(true));
    }

    @Test
    public void whenNoButtonsModeIsUsed_shouldViewBeLongClickableToSupportRemovingAnswers() {
        List<SelectChoice> items = getTestChoices();
        setUpFormEntryPrompt(items, "no-buttons");

        SelectItemClickListener listener = mock(SelectItemClickListener.class);
        SelectMultipleListAdapter adapter = new SelectMultipleListAdapter(new ArrayList<>(), listener, activityController.get(), items, formEntryPrompt, null, null, 0, 1, true);

        recyclerView.initRecyclerView(adapter, false);

        assertThat(getChoiceView(0).isLongClickable(), is(true));
    }

    @Test
    public void whenChangingAnswer_shouldHasAnswerChangedReturnCorrectValue() {
        List<SelectChoice> items = getTestChoices();
        setUpFormEntryPrompt(items, "");

        SelectItemClickListener listener = mock(SelectItemClickListener.class);
        List<Selection> selectedItems = new ArrayList<>();
        selectedItems.add(items.get(0).selection());
        SelectMultipleListAdapter adapter = new SelectMultipleListAdapter(selectedItems, listener, activityController.get(), items, formEntryPrompt, null, null, 0, 1, false);

        recyclerView.initRecyclerView(adapter, false);

        clickChoice(1); // Select BBB
        assertThat(adapter.hasAnswerChanged(), is(true));

        clickChoice(1); // Unselect BBB
        assertThat(adapter.hasAnswerChanged(), is(false));

        clickChoice(0); // Unselect AAA
        assertThat(adapter.hasAnswerChanged(), is(true));
    }

    private List<SelectChoice> getTestChoices() {
        return asList(
                new SelectChoice("AAA", "AAA"),
                new SelectChoice("BBB", "BBB")
        );
    }

    private void setUpFormEntryPrompt(List<SelectChoice> items, String appearance) {
        formEntryPrompt = new MockFormEntryPromptBuilder()
                .withSelectChoices(items)
                .withAppearance(appearance)
                .build();
    }

    private void clickChoice(int index) {
        if (WidgetAppearanceUtils.isNoButtonsAppearance(formEntryPrompt)) {
            clickNoButtonChoice(index);
        } else {
            clickButtonChoice(index);
        }
    }

    private void clickNoButtonChoice(int index) {
        recyclerView.getChildAt(index).performClick();
    }

    private void clickButtonChoice(int index) {
        ((AudioVideoImageTextLabel) getChoiceView(index)).getLabelTextView().performClick();
    }

    private void assertVisibleItemsInButtonsMode(String... items) {
        assertThat(recyclerView.getAdapter().getItemCount(), is(items.length));
        for (int i = 0; i < getVisibleItems().size(); i++) {
            if (recyclerView.getAdapter() instanceof SelectOneListAdapter) {
                assertThat(getRadioButton(i).getText().toString(), is(items[i]));
            } else {
                assertThat(getCheckBox(i).getText().toString(), is(items[i]));
            }
        }
    }

    private void assertVisibleItemsInNoButtonsMode(String... items) {
        assertThat(recyclerView.getAdapter().getItemCount(), is(items.length));
        for (int i = 0; i < getVisibleItems().size(); i++) {
            assertThat(((TextView) getChoiceView(i).getChildAt(0)).getText().toString(), is(items[i]));
        }
    }

    private List<SelectChoice> getVisibleItems() {
        return ((AbstractSelectListAdapter) recyclerView.getAdapter())
                .getFilteredItems();
    }

    private RadioButton getRadioButton(int index) {
        return (RadioButton) getAudioVideoImageTextLabelView(index).getLabelTextView();
    }

    private CheckBox getCheckBox(int index) {
        return (CheckBox) getAudioVideoImageTextLabelView(index).getLabelTextView();
    }

    private ViewGroup getChoiceView(int index) {
        return (ViewGroup) recyclerView.getChildAt(index);
    }

    private AudioVideoImageTextLabel getAudioVideoImageTextLabelView(int index) {
        return (AudioVideoImageTextLabel) recyclerView.getChildAt(index);
    }

    private boolean isItemSelected(int index) {
        return WidgetAppearanceUtils.isNoButtonsAppearance(formEntryPrompt)
                ? isNoButtonItemSelected(index)
                : isButtonItemSelected(index);
    }

    private boolean isNoButtonItemSelected(int index) {
        return getChoiceView(index).getBackground() != null;
    }

    private boolean isButtonItemSelected(int index) {
        return recyclerView.getAdapter() instanceof SelectOneListAdapter
                ? getRadioButton(index).isChecked()
                : getCheckBox(index).isChecked();
    }
}
