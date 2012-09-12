package glredbook10;


import javax.media.opengl.GLAutoDrawable;

import com.jogamp.opengl.util.FPSAnimator;

public abstract class GLSkeleton<D extends GLAutoDrawable> {

    public final D drawable;

    protected FPSAnimator animator;
    protected int FramePerSecond = 24;


    public GLSkeleton() {
        drawable = createDrawable();
    }

    protected abstract D createDrawable();

    /**
     * Call the reference canvas's display methods. Should be called after
     * handling of input events.
     */
    public final void refresh() {
        if (drawable == null)
            throw new RuntimeException("GLDrawable is not set.");
        drawable.display();
    }//


    public final void setAnimator(FPSAnimator animator) {
        this.animator = animator;
    }//

    public final void runExit() {
        new Thread(new Runnable() {
            public void run() {
                if(animator!=null)
                    animator.stop();
                drawable.destroy();
            }
        }).start();

    }//

}//
