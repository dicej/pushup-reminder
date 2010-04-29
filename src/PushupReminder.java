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
  private static final int MillisPerSecond = 1000;
  private static final int MillisPerMinute = MillisPerSecond * 60;
  private static final int MillisPerHour = MillisPerMinute * 60;
  
  public static void main(String[] args) {
    final String title = "Pushup Reminder";
    final String message = "Time to do pushups!";

    final Display display = new Display();
    final Tray tray = display.getSystemTray();
    if (tray != null) {
      final Shell shell = new Shell(display);

      final TrayItem item = new TrayItem(tray, SWT.NONE);

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

      long now = System.currentTimeMillis();
      long reminderTime = roundUp(now, MillisPerHour);
      long updateTime = 0;

      while (! shell.isDisposed()) {
        if (now > updateTime) {
          updateTime = roundUp(now, MillisPerSecond);
          item.setToolTipText(title + " - "
                              + minutes((int) (reminderTime - now)) + ":"
                              + seconds((int) (reminderTime - now))
                              + " remaining");

          display.timerExec((int) (updateTime - now), new Runnable() {
              public void run() { }
            });
        }

        if (now > reminderTime) {
          reminderTime = roundUp(now, MillisPerHour);
          alert(shell, item, title, message);
        }

        if (! display.readAndDispatch()) {
          display.sleep();
        }

        now = System.currentTimeMillis();
      }

      tray.dispose();
    } else {
      System.err.println("no tray!");
    }
  }

  private static long roundUp(long numerator, long divisor) {
    return (((numerator - 1) / divisor) * divisor) + divisor;
  }

  private static String minutes(int milliseconds) {
    return pad(milliseconds / MillisPerMinute, 2);
  }

  private static String seconds(int milliseconds) {
    return pad((milliseconds % MillisPerMinute) / MillisPerSecond, 2);
  }

  private static int pow(int n, int power) {
    int x = 1;
    for (; power > 0; -- power) {
      x *= n;
    }
    return x;
  }

  private static String pad(int number, int width) {
    StringBuilder sb = new StringBuilder();
    for (int i = pow(10, width - 1); i > 0; i /= 10) {
      if (number < i) {
        sb.append('0');
      }
    }
    if (number != 0) {
      sb.append(number);
    }
    return sb.toString();
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
