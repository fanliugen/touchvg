package touchvg.view;


import android.view.View;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import touchvg.skiaview.Floats;
import touchvg.skiaview.GiCanvasBase;
import touchvg.skiaview.GiContext;

public class GiCanvasEx extends GiCanvasBase{
    private Path mPath = new Path();
    private Paint mPen = new Paint();
    private Paint mBrush = new Paint();
    private Canvas mCanvas = null;
    private View mView = null;
    private static final float patDash[]      = { 5, 5 };
    private static final float patDot[]       = { 1, 2 };
    private static final float patDashDot[]   = { 10, 2, 2, 2 };
    private static final float dashDotdot[]   = { 20, 2, 2, 2, 2, 2 };
    private PathEffect mEffects = null;
    
	public GiCanvasEx(View view)
    {
        mView = view;
    }
    
    public Canvas getCanvas() {
        return mCanvas;
    }

    public boolean beginPaint(Canvas canvas) {
        if (this.mCanvas != null || canvas == null) {
            return false;
        }
        
        this.mCanvas = canvas;
        super.beginPaint();
        
        mPen.setAntiAlias(true);
        mPen.setDither(true);
        mPen.setStyle(Paint.Style.STROKE);
        mPen.setStrokeJoin(Paint.Join.ROUND);
        mPen.setStrokeCap(Paint.Cap.ROUND);
        mBrush.setStyle(Paint.Style.FILL);
        
        return true;
    }
    
    public void endPaint() {
        this.mCanvas = null;
        super.endPaint();
    }
    
    @Override
    public void setNeedRedraw() {
        mView.invalidate();
    }
    
    @Override
    public void antiAliasModeChanged(boolean alias) {
        mPen.setAntiAlias(alias);
    }
    
    private void makeLinePattern(float arr[], float penWidth)
    {
        float f[] = new float[arr.length];
        for (int i = 0; i < arr.length; i++) {
            f[i] = arr[i] * (penWidth < 1 ? 1 : penWidth);
        }
        this.mEffects = new DashPathEffect(f, 1);
    }

    @Override
    public void penChanged(GiContext context, float penWidth) {
        mPen.setColor(context.getLineARGB());
        mPen.setStrokeWidth(penWidth);
        
        int lineStyle = context.getLineStyle();
        
        if (lineStyle == 1)
            this.makeLinePattern(patDash, penWidth);
        else if (lineStyle == 2)
            this.makeLinePattern(patDot, penWidth);
        else if (lineStyle == 3)
            this.makeLinePattern(patDashDot, penWidth);
        else if (lineStyle == 4)
            this.makeLinePattern(dashDotdot, penWidth);
        else
            this.mEffects = null;
        mPen.setPathEffect(this.mEffects);
        context.delete();
        context = null;
    }
    
    @Override
    public void brushChanged(GiContext context) {
        mBrush.setColor(context.getFillARGB());
        context.delete();
        context = null;
    }

    @Override
    public boolean beginPath() {
        mPath.reset();
        return true;
    }
    
    @Override
    public boolean pathMoveTo(float x, float y) {
        mPath.moveTo(x, y);
        return true;
    }
    
    @Override
    public boolean pathLineTo(float x, float y) {
        mPath.lineTo(x, y);
        return true;
    }
    
    @Override
    public boolean pathBezierTo(Floats pxs) {
        for (int i = 0; i+5 < pxs.count(); i += 6) {
            mPath.cubicTo(pxs.get(i), pxs.get(i+1), pxs.get(i+2), 
                    pxs.get(i+3), pxs.get(i+4), pxs.get(i+5));
        }
        pxs.delete();
        pxs = null;
        return true;
    }
    
    @Override
    public boolean closePath() {
        mPath.close();
        return true;
    }
    
    @Override
    public boolean endPath(boolean stroke, boolean fill) {
        if (fill)
            mCanvas.drawPath(mPath, mBrush);
        if (stroke)
            mCanvas.drawPath(mPath, mPen);
        return true;
    }

    @Override
    public boolean drawBeziers(Floats pxs) {
        boolean ret = pxs.count() >= 8;
        Path p = new Path();
        
        if (ret) {
            p.moveTo(pxs.get(0), pxs.get(1));
            for (int i = 2; i+5 < pxs.count(); i += 6) {
                p.cubicTo(pxs.get(i), pxs.get(i+1), pxs.get(i+2), 
                        pxs.get(i+3), pxs.get(i+4), pxs.get(i+5));
            }
            mCanvas.drawPath(p, mPen);
        }
        pxs.delete();
        pxs=null;
        return ret;
    }
    
    @Override
    public boolean drawRect(float x, float y, float w, float h, 
                            boolean stroke, boolean fill) {
        if (fill)
            mCanvas.drawRect(x, y, x+w, y+h, mBrush);
        if (stroke)
            mCanvas.drawRect(x, y, x+w, y+h, mPen);
        return true;
    }

    @Override
    public boolean drawEllipse(float x, float y, float w, float h,
                               boolean stroke, boolean fill) {
        if (fill)
            mCanvas.drawOval(new RectF(x, y, x+w, y+h), mBrush);
        if (stroke)
            mCanvas.drawOval(new RectF(x, y, x+w, y+h), mPen);
        return true;
    }

    @Override
    public boolean drawLine(float x1, float y1, float x2, float y2) {
        mCanvas.drawLine(x1, y1, x2, y2, mPen);
        return true;
    }

    @Override
    public boolean drawLines(Floats pxs) {
        float []f = new float[pxs.count()];
        for (int i = 0; i < pxs.count(); i++) {
            f[i] = pxs.get(i);
        }
        mCanvas.drawLines(f, mPen);
        pxs.delete();
        pxs=null;
        return true;
    }

    @Override
    public boolean drawPolygon(Floats pxs, boolean stroke, boolean fill) {
        boolean ret = pxs.count() >= 4;
        Path p = new Path();
        
        if (ret) {
            p.moveTo(pxs.get(0), pxs.get(1));
            for (int i = 2; i + 1 < pxs.count(); i += 2) {
                p.lineTo(pxs.get(i), pxs.get(i+1));
            }
            p.close();
            
            if (fill)
                mCanvas.drawPath(p, mBrush);
            if (stroke)
                mCanvas.drawPath(p, mPen);
        }
        pxs.delete();
        pxs=null;
        return ret;
    }
    
}
