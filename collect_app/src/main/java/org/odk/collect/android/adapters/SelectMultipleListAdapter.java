/*
 * Copyright 2018 Nafundi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.odk.collect.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.content.ContextCompat;

import org.javarosa.core.model.SelectChoice;
import org.javarosa.core.model.data.helper.Selection;
import org.javarosa.core.reference.ReferenceManager;
import org.javarosa.form.api.FormEntryPrompt;
import org.odk.collect.android.R;
import org.odk.collect.android.audio.AudioHelper;
import org.odk.collect.android.formentry.questions.AudioVideoImageTextLabel;
import org.odk.collect.android.listeners.SelectItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class SelectMultipleListAdapter extends AbstractSelectListAdapter {

    private final List<Selection> originallySelectedItems;
    private final List<Selection> selectedItems;
    protected SelectItemClickListener listener;

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public SelectMultipleListAdapter(List<Selection> selectedItems, SelectItemClickListener listener,
                                     Context context, List<SelectChoice> items,
                                     FormEntryPrompt prompt, ReferenceManager referenceManager, AudioHelper audioHelper,
                                     int playColor, int numColumns, boolean noButtonsMode) {
        super(context, items, prompt, referenceManager, audioHelper, playColor, numColumns, noButtonsMode);
        this.originallySelectedItems = new ArrayList<>(selectedItems);
        this.selectedItems = selectedItems;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(noButtonsMode
                ? new FrameLayout(parent.getContext())
                : new AudioVideoImageTextLabel(context));
    }

    class ViewHolder extends AbstractSelectListAdapter.ViewHolder {
        ViewHolder(View v) {
            super(v);
            if (noButtonsMode) {
                view = (FrameLayout) v;
            } else {
                audioVideoImageTextLabel = (AudioVideoImageTextLabel) v;
                audioVideoImageTextLabel.setPlayTextColor(playColor);
                audioVideoImageTextLabel.setItemClickListener(listener);
                adjustAudioVideoImageTextLabelParams();
            }
        }

        void bind(final int index) {
            super.bind(index);
            if (noButtonsMode) {
                view.setBackground(null);
                for (Selection selectedItem : selectedItems) {
                    if (filteredItems.get(index).getValue().equals(selectedItem.getValue())) {
                        view.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.select_item_border));
                        break;
                    }
                }
            }
        }
    }

    @Override
    CheckBox createButton(final int index, ViewGroup parent) {
        AppCompatCheckBox checkBox = (AppCompatCheckBox) LayoutInflater.from(parent.getContext()).inflate(R.layout.select_multi_item, null);
        setUpButton(checkBox, index);
        checkCheckBoxIfNeeded(checkBox, index); // perform before setting onCheckedChangeListener to avoid redundant calls of its body

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                addItem(filteredItems.get(index).selection());
            } else {
                removeItem(filteredItems.get(index).selection());
            }
            if (listener != null) {
                listener.onItemClicked();
            }
        });

        return checkBox;
    }

    private void checkCheckBoxIfNeeded(CheckBox checkBox, int index) {
        for (Selection selectedItem : selectedItems) {
            // match based on value, not key
            if (filteredItems.get(index).getValue().equals(selectedItem.getValue())) {
                checkBox.setChecked(true);
                break;
            }
        }
    }

    @Override
    void onItemClick(Selection selection, View view) {
        if (isItemSelected(selectedItems, selection)) {
            removeItem(selection);
            if (view != null) {
                view.setBackground(null);
            }
        } else {
            addItem(selection);
            if (view != null) {
                view.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.select_item_border));
            }
        }
    }

    public void addItem(Selection item) {
        if (!isItemSelected(selectedItems, item)) {
            selectedItems.add(item);
        }
    }

    public void removeItem(Selection item) {
        for (Selection selectedItem : selectedItems) {
            if (selectedItem.getValue().equals(item.getValue())) {
                selectedItems.remove(selectedItem);
                break;
            }
        }
    }

    @Override
    public void clearAnswer() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public List<Selection> getSelectedItems() {
        return selectedItems;
    }

    @Override
    public boolean hasAnswerChanged() {
        if (originallySelectedItems.size() != selectedItems.size()) {
            return true;
        }
        for (Selection item : originallySelectedItems) {
            boolean foundEqualElement = false;
            for (Selection item2 : selectedItems) {
                if (item.xmlValue.equals(item2.xmlValue)) {
                    foundEqualElement = true;
                    break;
                }
            }
            if (!foundEqualElement) {
                return false;
            }
        }

        return false;
    }
}