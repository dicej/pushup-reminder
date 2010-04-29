import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.graphics.Image;

public class PushupReminder {
  public static void main(String[] args) {
    final String title = "Pushup Reminder";
    final String message = "Time to do pushups!";

    final Display display = new Display();
    final Tray tray = display.getSystemTray();
    if (tray != null) {
      final Shell shell = new Shell(display);

      final TrayItem item = new TrayItem(tray, SWT.NONE);
      item.setToolTipText(title);

      Image icon = new Image(display, 32, 32);
      item.setImage(icon);

      Listener clickListener = new Listener() {
          public void handleEvent(Event event) {
            Menu menu = new Menu(shell, SWT.POP_UP);

            MenuItem exitItem = new MenuItem(menu, SWT.PUSH);
            exitItem.setText("exit");
            exitItem.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent event) {
                  shell.dispose();
                }
              });

            menu.setVisible(true);
          }
        };
      item.addListener(SWT.MenuDetect, clickListener);
      item.addListener(SWT.Selection, clickListener);

      final boolean[] startTimer = new boolean[] { true };
      while (! shell.isDisposed()) {
        if (startTimer[0]) {
          startTimer[0] = false;
          display.timerExec(timeUntilTopOfHour(), new Runnable() {
              public void run() {
                alert(shell, item, title, message);
                startTimer[0] = true;
              }
            });
        }

        if (! display.readAndDispatch()) {
          display.sleep();
        }
      }

      tray.dispose();
    } else {
      System.err.println("no tray!");
    }
  }

  private static int timeUntilTopOfHour() {
    final long MillisPerHour = 60 * 60 * 1000;
    long now = System.currentTimeMillis();
    long then = (((now - 1) / MillisPerHour) * MillisPerHour) + MillisPerHour;
    System.out.println("waiting " + (then - now) + "ms");
    return (int) (then - now);
  }

  private static void alert(Shell shell,
                            TrayItem item,
                            String title,
                            String message)
  {
    ToolTip tip = new ToolTip(shell, SWT.BALLOON | SWT.ICON_INFORMATION);

    tip.setText(title);
    tip.setMessage(message);
    item.setToolTip(tip);
    tip.setVisible(true);
  }
}
