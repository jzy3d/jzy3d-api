package org.jzy3d.replay.old;
import java.awt.*;
import java.awt.event.*;

public class MouseDragRobotTest {

    static final String TEST_DELAY_FACTOR = "TEST_DELAY_FACTOR";
    static final String KEY_DELAY_FACTOR = "KEY_DELAY_FACTOR";
    private static int delay = 500;
    private static int keyDelay = 50;

    private boolean passed = true;
    private boolean focusGained = false;
    private Robot robot;
    private Frame frame;
    private Canvas canvas;
    private Object focusLock = new Object();
    private Object dragLock = new Object();

    private boolean mouseDragged = false;
    private int xMousePos, yMousePos;

    public static void main(String[] args) {
        String stepDelayString = System.getProperty(TEST_DELAY_FACTOR);
        if (stepDelayString != null) {
            try {
                delay = Integer.parseInt(stepDelayString);
            } catch (Exception e) {
            }
        }
        String keyDelayString = System.getProperty(KEY_DELAY_FACTOR);
        if (keyDelayString != null) {
            try {
                keyDelay = Integer.parseInt(keyDelayString);
            } catch (Exception e) {
            }
        }
        try {
            MouseDragRobotTest test = new MouseDragRobotTest();
            test.doTest();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public MouseDragRobotTest() {
        try {
            Toolkit.getDefaultToolkit().getSystemEventQueue().invokeAndWait(new Runnable() {
                public void run() {
                    initializeGUI();
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void initializeGUI() {
        frame = new Frame("Test frame");
        canvas = new Canvas();
        canvas.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent event) {
                focusGained = true;
                synchronized (focusLock) {
                    try {
                        focusLock.notifyAll();
                    } catch (Exception e) {
                    }
                }
            }

            public void focusLost(FocusEvent event) {
            }
        });
        canvas.setBounds(20, 20, 100, 100);
        canvas.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                xMousePos = e.getX();
                yMousePos = e.getY();
System.err.println(xMousePos + ", "+ yMousePos);
                mouseDragged = true;
                synchronized (dragLock) {
                    try {
                        dragLock.notifyAll();
                    } catch (Exception ex) {
                    }
                }
                System.err.println("DRAGGING");
            }
        });
        
        canvas.addMouseListener(new MouseAdapter(){
			 public void mouseDragged(MouseEvent e){
				 System.err.println("DRAGGING");
			 }
		});
        
        frame.setLayout(new BorderLayout());
        frame.add(canvas);
        frame.setSize(200, 200);
        frame.setLocation(100, 100);
        frame.setVisible(true);
    }

    public void doTest() throws Exception {
        try {
            robot = new Robot();
            Thread.sleep(delay * 10);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        robot.mouseMove((int) frame.getLocationOnScreen().getX() + frame.getSize().width / 2,
                        (int) frame.getLocationOnScreen().getY() + frame.getSize().height / 2);
        robot.delay(delay);
        robot.mousePress(MouseEvent.BUTTON1_MASK);
        robot.delay(delay);
        robot.mouseRelease(MouseEvent.BUTTON1_MASK);
        robot.delay(delay);

        if (!focusGained) {
            synchronized (focusLock) {
                try {
                    focusLock.wait(delay * 5);
                } catch (Exception e) {
                }
            }
        }
        if (!focusGained) {
            System.err.println("FAIL: Canvas failed to gain focus!");
            System.exit(1);
        }

        mouseDragged = false;
        robot.mouseMove((int) canvas.getLocationOnScreen().x + 10,
                        (int) canvas.getLocationOnScreen().y + canvas.getHeight()/2);
        robot.delay(delay);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.delay(delay);
        for(int i = 11; i <= canvas.getWidth() / 2; i++) {
            robot.delay(20);
            robot.mouseMove((int) canvas.getLocationOnScreen().x + i,
                            (int) canvas.getLocationOnScreen().y + canvas.getHeight()/2);
        }
        robot.delay(delay);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        robot.delay(delay);

        if (! mouseDragged) {
            synchronized (dragLock) {
                try {
                    dragLock.wait(delay * 10);
                } catch (Exception e) {
                }
            }
        }
        if (! mouseDragged) {
            System.err.println("FAIL: MouseDrag Event is triggered not when"+
                               " mouse move is performed with the mouse button pressed");
            passed = false;
        }

        mouseDragged = false;
        robot.mouseMove((int) canvas.getLocationOnScreen().x,
                        (int) canvas.getLocationOnScreen().y);
        robot.delay(delay);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.delay(delay);
        for(int i = 1; i < canvas.getHeight() / 2; i++) {
            robot.delay(20);
            robot.mouseMove((int) canvas.getLocationOnScreen().x,
                            (int) canvas.getLocationOnScreen().y + i);
        }
        robot.delay(delay);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);

        if (! mouseDragged) {
            synchronized (dragLock) {
                try {
                    dragLock.wait(delay * 10);
                } catch (Exception e) {
                }
            }
        }
        if (! mouseDragged) {
            System.err.println("FAIL: MouseDrag Event is not triggered when"+
                               " mouse move is performed on the edge of the component with"+
                               " the mouse button pressed");
            passed = false;
        }

        //mouse drag above the component
        int x = (int) canvas.getLocationOnScreen().x + canvas.getWidth()/2;
        int y = (int) canvas.getLocationOnScreen().y + canvas.getHeight()/2;

        robot.delay(delay);
        robot.mouseMove(x, y);
        robot.delay(delay);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.delay(delay);

        mouseDragged = false;
        xMousePos = 0;
        yMousePos = 0;

        for(int i = 0; i <= 30; i++) {
            robot.mouseMove(canvas.getLocationOnScreen().x - i, y);
            robot.delay(20);
        }

        for(int i = 0; i <= canvas.getHeight() / 2 + 20; i++) {
            robot.mouseMove(canvas.getLocationOnScreen().x - 30, y - i);
            robot.delay(20);
        }

        int xExpected = -30;
        int yExpected = -20;

        robot.delay(delay);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);

        if (xExpected != xMousePos || yExpected != yMousePos) {
            synchronized (dragLock) {
                try {
                    dragLock.wait(delay * 10);
                } catch (Exception e) {
                }
            }
        }
        if (xExpected != xMousePos || yExpected != yMousePos) {
            System.err.println("FAIL: Mouse has not moved to specified Location : X = " +
                               xExpected + " Y = " + yExpected);
            System.err.println("Returned values: xMousePos=" + xMousePos +
                               " yMousePos=" + yMousePos);
            passed = false;
        }

        if (passed) {
            System.out.println("Test passed");
            System.exit(0);
        } else {
            System.err.println("Test failed");
            System.exit(1);
        }
    }
}
