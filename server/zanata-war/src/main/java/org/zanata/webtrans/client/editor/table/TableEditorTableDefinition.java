/*
 * Copyright 2010, Red Hat, Inc. and individual contributors as indicated by the
 * @author tags. See the copyright.txt file in the distribution for a full
 * listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.zanata.webtrans.client.editor.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.customware.gwt.presenter.client.EventBus;

import org.zanata.webtrans.client.events.CopySourceEvent;
import org.zanata.webtrans.client.resources.NavigationMessages;
import org.zanata.webtrans.client.ui.HighlightingLabel;
import org.zanata.webtrans.client.ui.TransUnitDetailsPanel;
import org.zanata.webtrans.shared.model.TransUnit;
import org.zanata.webtrans.shared.model.TransUnitId;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gen2.table.client.AbstractColumnDefinition;
import com.google.gwt.gen2.table.client.CellRenderer;
import com.google.gwt.gen2.table.client.ColumnDefinition;
import com.google.gwt.gen2.table.client.DefaultTableDefinition;
import com.google.gwt.gen2.table.client.RowRenderer;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class TableEditorTableDefinition extends DefaultTableDefinition<TransUnit>
{

   // public static final int INDICATOR_COL = 0;
   public static final int SOURCE_COL = 0;
   public static final int TARGET_COL = 1;

   private final boolean isReadOnly;

   private String findMessage;
   private SourcePanel topSourcePanel;
   private ArrayList<Widget> copyButtons;
   private boolean showingCopyButtons;
   private EventBus eventBus;
   
   private TransUnitDetailsPanel transUnitDetailsContent;

   private final RowRenderer<TransUnit> rowRenderer = new RowRenderer<TransUnit>()
   {
      @Override
      public void renderRowValue(TransUnit rowValue, AbstractRowView<TransUnit> view)
      {
         String styles = "TableEditorRow ";
         styles += view.getRowIndex() % 2 == 0 ? "odd-row" : "even-row";


         String state = "";
         switch (rowValue.getStatus())
         {
         case Approved:
            state = " Approved";
            break;
         case NeedReview:
            state = " Fuzzy";
            break;
         case New:
            state = " New";
            break;
         }
         styles += state + "StateDecoration";

         view.setStyleName(styles);
      }
   };

   // private final AbstractColumnDefinition<TransUnit, TransUnit>
   // indicatorColumnDefinition =
   // new AbstractColumnDefinition<TransUnit, TransUnit>() {
   // @Override
   // public TransUnit getCellValue(TransUnit rowValue) {
   // return rowValue;
   // }
   //
   // @Override
   // public void setCellValue(TransUnit rowValue, TransUnit cellValue) {
   // cellValue.setSource(rowValue.getSource());
   // }
   // };
   //
   // private final CellRenderer<TransUnit, TransUnit> indicatorCellRenderer =
   // new CellRenderer<TransUnit, TransUnit>() {
   // @Override
   // public void renderRowValue(
   // TransUnit rowValue,
   // ColumnDefinition<TransUnit, TransUnit> columnDef,
   // com.google.gwt.gen2.table.client.TableDefinition.AbstractCellView<TransUnit>
   // view) {
   // view.setStyleName("TableEditorCell TableEditorCell-Source");
   // if(rowValue.getEditStatus().equals(EditState.Lock)) {
   // Image image = new Image("../img/silk/user.png");
   // view.setWidget(image);
   // }
   // }
   // };

   private final AbstractColumnDefinition<TransUnit, TransUnit> sourceColumnDefinition = new AbstractColumnDefinition<TransUnit, TransUnit>()
   {
      @Override
      public TransUnit getCellValue(TransUnit rowValue)
      {
         return rowValue;
      }

      @Override
      public void setCellValue(TransUnit rowValue, TransUnit cellValue)
      {
         cellValue.setSource(rowValue.getSource());
         cellValue.setSourceComment(rowValue.getSourceComment());
      }
   };

   private Map<TransUnitId, VerticalPanel> sourcePanelMap = new HashMap<TransUnitId, VerticalPanel>();

   private final CellRenderer<TransUnit, TransUnit> sourceCellRenderer = new CellRenderer<TransUnit, TransUnit>()
   {
      @Override
      public void renderRowValue(final TransUnit rowValue, ColumnDefinition<TransUnit, TransUnit> columnDef, AbstractCellView<TransUnit> view)
      {
         view.setStyleName("TableEditorCell TableEditorCell-Source");
         VerticalPanel panel = new VerticalPanel();
         panel.setSize("100%", "100%");

         TableResources images = GWT.create(TableResources.class);
         // final Image copyButton = new Image(images.copySrcButton());
         final PushButton copyButton = new PushButton(new Image(images.copySrcButton()));
         copyButton.setStyleName("gwt-Button");
         copyButton.setSize("16px", "16px");
         copyButton.setTitle(messages.copySourcetoTarget());
         copyButton.setVisible(showingCopyButtons);
         copyButton.addClickHandler(new ClickHandler()
         {
            @Override
            public void onClick(ClickEvent event)
            {
               eventBus.fireEvent(new CopySourceEvent(rowValue));
            }

         });
         
         topSourcePanel = new SourcePanel(rowValue, images, messages);
         
         if (findMessage != null && !findMessage.isEmpty())
         {
            topSourcePanel.highlightSearch(findMessage);
         }
         topSourcePanel.getLabel().sinkEvents(Event.ONCLICK);
         topSourcePanel.getLabel().addClickHandler(new ClickHandler()
         {
            @Override
            public void onClick(ClickEvent event)
            {
               if (targetCellEditor.isOpened())
               {
                  targetCellEditor.savePendingChange(true);
               }
            }

         });

         topSourcePanel.add(copyButton);
         // topSourcePanel.add(temp);
         copyButtons.add(copyButton);
         
         panel.add(topSourcePanel);
         sourcePanelMap.put(rowValue.getId(), panel);

         view.setWidget(panel);
      }
   };

   public void setShowCopyButtons(boolean showButtons)
   {
      showingCopyButtons = showButtons;
      for (Widget btns : copyButtons)
      {
         btns.setVisible(showButtons);
      }
   }

   private final AbstractColumnDefinition<TransUnit, TransUnit> targetColumnDefinition = new AbstractColumnDefinition<TransUnit, TransUnit>()
   {

      @Override
      public TransUnit getCellValue(TransUnit rowValue)
      {
         return rowValue;
      }

      @Override
      public void setCellValue(TransUnit rowValue, TransUnit cellValue)
      {
         cellValue.setTarget(rowValue.getTarget());
      }

   };

   private final CellRenderer<TransUnit, TransUnit> targetCellRenderer = new CellRenderer<TransUnit, TransUnit>()
   {
      @Override
      public void renderRowValue(TransUnit rowValue, ColumnDefinition<TransUnit, TransUnit> columnDef, AbstractCellView<TransUnit> view)
      {
         view.setStyleName("TableEditorCell TableEditorCell-Target");
         final VerticalPanel targetPanel = new VerticalPanel();

         final Label label = new HighlightingLabel();

         // if editor is opening, do not render target cell, otherwise editor
         // will be closed
         // targetCellEditor.isEditing not suitable since when we click the save
         // button, cellValue is not null.

         if (targetCellEditor.isOpened() && targetCellEditor.getTargetCell().getId().equals(rowValue.getId()))
         {
            return;
         }

         if (rowValue.getTarget().isEmpty() && !isReadOnly)
         {
            label.setText(messages.clickHere());
            label.setStylePrimaryName("TableEditorContent-Empty");
         }
         else
         {
            label.setText(rowValue.getTarget());
            label.setStylePrimaryName("TableEditorContent");
         }

         if (findMessage != null && !findMessage.isEmpty())
         {
            ((HighlightingLabel) label).highlightSearch(findMessage);
         }
         label.setTitle(messages.clickHere());
         targetPanel.add(label);

         targetPanel.setSize("100%", "100%");

         view.setWidget(targetPanel);
      }
   };

   private InlineTargetCellEditor targetCellEditor;
   private final NavigationMessages messages;

   public void setFindMessage(String findMessage)
   {
      this.findMessage = findMessage;
   }

   public TableEditorTableDefinition(final NavigationMessages messages, final RedirectingCachedTableModel<TransUnit> tableModel, final EventBus eventBus, boolean isReadOnly)
   {
      this.isReadOnly = isReadOnly;
      this.messages = messages;
      this.eventBus = eventBus;
      setRowRenderer(rowRenderer);
      sourceColumnDefinition.setCellRenderer(sourceCellRenderer);
      targetColumnDefinition.setCellRenderer(targetCellRenderer);
      CancelCallback<TransUnit> cancelCallBack = new CancelCallback<TransUnit>()
      {
         @Override
         public void onCancel(TransUnit cellValue)
         {
            tableModel.onCancel(cellValue);
         }
      };
      EditRowCallback transValueCallBack = new EditRowCallback()
      {
         @Override
         public void gotoNextRow()
         {
            tableModel.gotoNextRow();
         }

         @Override
         public void gotoPrevRow()
         {
            tableModel.gotoPrevRow();
         }

         @Override
         public void gotoFirstRow()
         {
            tableModel.gotoFirstRow();
         }

         @Override
         public void gotoLastRow()
         {
            tableModel.gotoLastRow();
         }

         @Override
         public void gotoNextFuzzyNewRow()
         {
            tableModel.gotoNextFuzzyNew();
         }

         @Override
         public void gotoPrevFuzzyNewRow()
         {
            tableModel.gotoPrevFuzzyNew();
         }

         @Override
         public void gotoNextFuzzyRow()
         {
            tableModel.gotoNextFuzzy();
         }

         @Override
         public void gotoPrevFuzzyRow()
         {
            tableModel.gotoPrevFuzzy();
         }

         @Override
         public void gotoNextNewRow()
         {
            tableModel.gotoNextNew();
         }

         @Override
         public void gotoPrevNewRow()
         {
            tableModel.gotoPrevNew();
         }
      };
      
      this.targetCellEditor = new InlineTargetCellEditor(messages, cancelCallBack, transValueCallBack, eventBus, isReadOnly);
      this.transUnitDetailsContent = new TransUnitDetailsPanel(messages.transUnitDetailsHeading());

      targetColumnDefinition.setCellEditor(targetCellEditor);

      // See _INDEX consts above if modifying!
      // addColumnDefinition(indicatorColumnDefinition);
      addColumnDefinition(sourceColumnDefinition);
      addColumnDefinition(targetColumnDefinition);

      copyButtons = new ArrayList<Widget>();
      showingCopyButtons = true;
   }

   public InlineTargetCellEditor getTargetCellEditor()
   {
      return targetCellEditor;
   }

   public void setTransUnitDetails(TransUnit selectedTransUnit)
   {
      VerticalPanel sourcePanel = sourcePanelMap.get(selectedTransUnit.getId());
      if (sourcePanel != null)
      {
         transUnitDetailsContent.setDetails(selectedTransUnit);
         sourcePanel.add(transUnitDetailsContent);
         sourcePanel.setCellVerticalAlignment(transUnitDetailsContent,HasVerticalAlignment.ALIGN_BOTTOM);
      }
   }

}
