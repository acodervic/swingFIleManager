package github.acodervic.mod.swing.text;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

import github.acodervic.mod.swing.MyComponent;

public class UndoTextField extends JTextField {
  private final UndoManager undoManager = new UndoManager();
  private final Document doc = new PlainDocument() {
    @Override
    public void replace(int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
      undoManager.undoableEditHappened(new UndoableEditEvent(this, new ReplaceUndoableEdit(offset, length, text)));
      replaceIgnoringUndo(offset, length, text, attrs);
    }

    private void replaceIgnoringUndo(int offset, int length, String text, AttributeSet attrs)
        throws BadLocationException {
      for (UndoableEditListener uel : getUndoableEditListeners()) {
        removeUndoableEditListener(uel);
      }
      super.replace(offset, length, text, attrs);
      for (UndoableEditListener uel : getUndoableEditListeners()) {
        addUndoableEditListener(uel);
      }
    }

    class ReplaceUndoableEdit extends AbstractUndoableEdit {
      private final String oldValue;
      private final String newValue;
      private int offset;

      public ReplaceUndoableEdit(int offset, int length, String newValue) {
        String txt;
        try {
          txt = getText(offset, length);
        } catch (BadLocationException e) {
          txt = null;
        }
        this.oldValue = txt;
        this.newValue = newValue;
        this.offset = offset;
      }

      @Override
      public void undo() throws CannotUndoException {
        try {
          replaceIgnoringUndo(offset, newValue.length(), oldValue, null);
        } catch (BadLocationException ex) {
          throw new CannotUndoException();
        }
      }

      @Override
      public void redo() throws CannotRedoException {
        try {
          replaceIgnoringUndo(offset, oldValue.length(), newValue, null);
        } catch (BadLocationException ex) {
          throw new CannotUndoException();
        }
      }

      @Override
      public boolean canUndo() {
        return true;
      }

      @Override
      public boolean canRedo() {
        return true;
      }
    }
  };

  public UndoTextField() {
    getDocument().addUndoableEditListener(undoManager);
    new MyComponent<>(this).onKeyDown(keye -> {
      if (keye.isControlDown() && keye.getKeyCode() == 90) {
        undoManager.undo();
      }
    });
  }

  public static void main(String[] args) {
    JFrame jFrame = new JFrame();
    jFrame.add(new UndoTextField());
    jFrame.show();
  }
}
