/*
 * Copyright 2011 Mikhail Lopatkin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package name.mlopatkin.andlogview.ui.filterpanel;

import name.mlopatkin.andlogview.ui.Icons;
import name.mlopatkin.andlogview.ui.themes.Theme;
import name.mlopatkin.andlogview.ui.themes.ThemedWidgetFactory;
import name.mlopatkin.andlogview.widgets.UiHelper;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;

public class FilterPanel extends FilterPanelUi implements FilterPanelModel.FilterPanelModelListener {
    private final ThemedWidgetFactory themed;
    private final FilterPanelModel model;
    private final FilterCreator filterCreator;
    private final Map<PanelFilterView, FilterButton> buttonByFilter = new HashMap<>();

    private final ImageIcon filterIcon;

    private final Action acScrollLeft;
    private final Action acScrollRight;

    private final PopupMenuHandler menuHandler = new PopupMenuHandler();

    private int leftmostButton = -1;
    private int rightmostButton = -1;

    @Inject
    public FilterPanel(Theme theme, FilterPanelModel model, FilterCreator filterCreator) {
        super((int) theme.getWidgetFactory().scale(36));
        this.themed = theme.getWidgetFactory();
        this.model = model;
        this.filterCreator = filterCreator;

        themed.configureFilterPanel(this, content);

        filterIcon = themed.getIcon(Icons.FILTER);
        ImageIcon addIcon = themed.getIcon(Icons.ADD);
        ImageIcon nextIcon = themed.getIcon(Icons.NEXT);
        ImageIcon prevIcon = themed.getIcon(Icons.PREVIOUS);

        model.addListener(this);

        ComponentListener resizeListener = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateScrollState();
            }
        };
        addComponentListener(resizeListener);

        Action createFilterAction = UiHelper.makeAction("", addIcon, filterCreator::createFilterWithDialog);
        acScrollLeft = UiHelper.makeAction("", prevIcon, this::scrollPanelLeft);
        acScrollRight = UiHelper.makeAction("", nextIcon, this::scrollPanelRight);

        btAddFilter.setAction(createFilterAction);
        btScrollLeft.setAction(acScrollLeft);
        btScrollRight.setAction(acScrollRight);

        UiHelper.addDoubleClickAction(this, createFilterAction);
        UiHelper.addDoubleClickAction(content, createFilterAction);

        themed.configureFilterPanelButton(btAddFilter);
        themed.configureFilterPanelScrollButton(btScrollLeft);
        themed.configureFilterPanelScrollButton(btScrollRight);

        for (PanelFilterView filter : model.getFilters()) {
            onFilterAdded(filter);
        }
    }

    private void scrollPanelLeft() {
        computeButtonIndices();
        if (leftmostButton > 0) {
            scrollTo(leftmostButton - 1);
        }
    }

    private void scrollPanelRight() {
        computeButtonIndices();
        if (rightmostButton < content.getComponentCount() - 1) {
            scrollTo(rightmostButton + 1);
        }
    }

    @Override
    public void onFilterAdded(PanelFilterView newFilter) {
        FilterButton button = new FilterButton(newFilter);
        buttonByFilter.put(newFilter, button);
        content.add(button);
        menuHandler.addPopup(button);
        revalidate();
        repaint();
    }

    @Override
    public void onFilterRemoved(PanelFilterView filter) {
        FilterButton button = buttonByFilter.remove(filter);
        if (button != null) {
            content.remove(button);
            revalidate();
            repaint();
        }
    }

    @Override
    public void onFilterReplaced(PanelFilterView oldFilter, PanelFilterView newFilter) {
        assert oldFilter != null;
        assert newFilter != null;

        FilterButton button = buttonByFilter.remove(oldFilter);
        assert button != null;
        assert button.getFilter() == oldFilter;
        assert buttonByFilter.get(newFilter) == null;

        button.setFilter(newFilter);
        buttonByFilter.put(newFilter, button);
    }

    @Override
    public void onFilterEnabled(PanelFilterView filter, boolean enabled) {
        FilterButton button = buttonByFilter.get(filter);
        assert button != null;
        button.setSelected(enabled);
    }

    private void computeButtonIndices() {
        Rectangle viewportRect = contentViewport.getBounds();
        Rectangle contentsRect = content.getBounds();
        viewportRect.x -= contentsRect.x;
        viewportRect.y -= contentsRect.y;
        int cur = 0;
        leftmostButton = -1;
        rightmostButton = -1;
        for (Component button : content.getComponents()) {
            Rectangle buttonRect = button.getBounds();
            if (viewportRect.contains(buttonRect)) {
                if (leftmostButton < 0) {
                    leftmostButton = cur;
                }
                rightmostButton = cur;
            }
            ++cur;
        }
    }

    private void scrollTo(int i) {
        Component button = content.getComponent(i);
        content.scrollRectToVisible(button.getBounds());
        updateScrollState();
    }

    private void updateScrollState() {
        computeButtonIndices();
        boolean canScrollLeft = leftmostButton > 0;
        boolean canScrollRight = rightmostButton < content.getComponentCount() - 1;
        boolean canScroll = canScrollLeft || canScrollRight;

        acScrollLeft.setEnabled(canScrollLeft);
        acScrollRight.setEnabled(canScrollRight);

        btScrollRight.setVisible(canScroll);
        sepScrollableLeft.setVisible(canScroll);
        sepScrollableRight.setVisible(canScroll);
        btScrollLeft.setVisible(canScroll);
    }

    private final class FilterButton extends JToggleButton implements ActionListener {
        private PanelFilterView filter;

        public FilterButton(PanelFilterView filter) {
            super(filterIcon, true);

            setFilter(filter);
            addActionListener(this);
            themed.configureFilterPanelButton(this);
        }

        public PanelFilterView getFilter() {
            return filter;
        }

        public void setFilter(PanelFilterView newFilter) {
            filter = newFilter;
            setSelected(filter.isEnabled());
            setToolTipText(filter.getTooltip());
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            FilterPanel.this.model.setFilterEnabled(filter, isSelected());
        }

        @Override
        public String toString() {
            return filter.toString() + " " + super.toString();
        }
    }

    private class PopupMenuHandler {
        private JPopupMenu menu = new JPopupMenu();
        private JMenuItem editItem = new JMenuItem("Edit filter");
        private JMenuItem removeItem = new JMenuItem("Remove filter");
        private @Nullable FilterButton activeButton;

        @SuppressWarnings("NullAway")
        PopupMenuHandler() {
            // TODO(mlopatkin) This can probably be rewritten to ensure that activeButton is nonnull.
            editItem.addActionListener(e -> model.editFilter(activeButton.getFilter()));
            removeItem.addActionListener(e -> model.removeFilter(activeButton.getFilter()));
            menu.add(editItem);
            menu.add(removeItem);
        }

        void addPopup(final FilterButton button) {
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        showMenu(e);
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        showMenu(e);
                    }
                }

                private void showMenu(MouseEvent e) {
                    activeButton = button;
                    menu.show(e.getComponent(), e.getX(), e.getY());
                }
            });
        }
    }
}
