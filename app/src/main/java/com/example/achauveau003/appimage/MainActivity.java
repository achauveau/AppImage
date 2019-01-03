package com.example.achauveau003.appimage;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.renderscript.RenderScript;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.renderscript.Allocation;


public class MainActivity extends AppCompatActivity{

    private ImageView img;
    private Bitmap bmap;
    private Bitmap bmap_copy;
    private BitmapFactory.Options options;
    private Button button;
    private Button button2;
    private Button button3;
    private Button button4;
    private Button button5;
    private Button button6;
    private Menu menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        img = findViewById(R.id.couleur1);
        options = new BitmapFactory.Options();
        options.inMutable=true;
        options.inScaled=false;
        bmap = BitmapFactory.decodeResource(getResources(),R.drawable.couleur1,options);
        bmap_copy = bmap.copy(Bitmap.Config.ARGB_8888,true);
        img.setImageBitmap(bmap);

        button = findViewById(R.id.button_id);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                toGreyRS(bmap);
            }
        });

        button2 = findViewById(R.id.button2_id);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                colorizeRS(bmap);
            }
        });

        button3 = findViewById(R.id.button3_id);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                keepRedRS(bmap);
            }
        });

        button4 = findViewById(R.id.button4_id);
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                reset();
            }
        });

        button5 = findViewById(R.id.button5_id);
        button5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                equalizationHistogramRS(bmap);
            }
        });

        button6 = findViewById(R.id.button6_id);
        button6.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                convolution(bmap,5,false);
            }
        });

        /*onCreateOptionsMenu(menu);
        MenuItem reset_item = menu.add(0,R.id.reset,0,"Reset");
        MenuItem toGreyRS_item = menu.add(1,R.id.toGreyRS,1,"toGreyRS");
        */





    }

    /*public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.activity_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.reset:
                reset();
                return true;
            case R.id.toGreyRS:
                toGreyRS(bmap);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/


    private void reset(){
        bmap = bmap_copy.copy(Bitmap.Config.ARGB_8888,true);
        img.setImageBitmap(bmap);
    }

    private void toGray(Bitmap bmp){
        int col ;
        for(int i=0;i<bmp.getWidth();i++){
            for(int j=0;j<bmp.getHeight();j++){
                col=bmp.getPixel(i,j);
                int r = Color.red(col);
                int g = Color.green(col);
                int b = Color.blue(col);
                double col1 = 0.3*r+0.59*g+0.11*b;
                bmp.setPixel(i,j,Color.argb(1,(int)col1,(int)col1,(int)col1));
            }
        }
    }

    private void toGrays(Bitmap bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int size =w*h;
        int pixels[]=new int[size];
        bmp.getPixels(pixels,0,w,0,0,w,h);
        for(int i=0 ;i<size;i++){
            int r = Color.red(pixels[i]);
            int g = Color.green(pixels[i]);
            int b = Color.blue(pixels[i]);
            double col1 = 0.3*r+0.59*g+0.11*b;
            pixels[i]=Color.argb(1,(int)col1,(int)col1,(int)col1);
        }
        bmp.setPixels(pixels,0,w,0,0,w,h);


    }

    private void colorize(Bitmap bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int pixels[]=new int[w*h];
        float hsv[]= new float[3];
        int random = (int)(Math.random()*360);
        bmp.getPixels(pixels,0,w,0,0,w,h);
        for(int i=0 ; i<(w*h) ; i++){
            int r = Color.red(pixels[i]);
            int g = Color.green(pixels[i]);
            int b = Color.blue(pixels[i]);
            Color.RGBToHSV(r,g,b,hsv);
            hsv[0]=random;
            int col = Color.HSVToColor(hsv);
            pixels[i]=col;
        }
        bmp.setPixels(pixels,0,w,0,0,w,h);
    }

    private void keepRed(Bitmap bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int pixels[]=new int[w*h];
        float hsv[]= new float[3];
        bmp.getPixels(pixels,0,w,0,0,w,h);
        for(int i=0 ; i<(w*h) ; i++) {
            int r = Color.red(pixels[i]);
            int g = Color.green(pixels[i]);
            int b = Color.blue(pixels[i]);
            Color.RGBToHSV(r, g, b, hsv);
            if (hsv[0] > 20 && hsv[0] < 340) {
                double col1 = 0.3 * r + 0.59 * g + 0.11 * b;
                pixels[i] = Color.argb(1, (int) col1, (int) col1, (int) col1);
            }
        }
        bmp.setPixels(pixels,0,w,0,0,w,h);
    }


    //extension dynamique en noir et blanc
    private void contrastV1(Bitmap bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int max = 0;
        int min = 255;
        int pixels[]=new int[w*h];
        int lut[]=new int[256];
        bmp.getPixels(pixels,0,w,0,0,w,h);
        for(int i=0 ; i<(w*h) ; i++) {
            int k = Color.red(pixels[i]);
            if(k<min)
                min = k;
            if(k>max)
                max = k;

        }
        for(int ng=0 ; ng<256 ; ng++) {
            lut[ng]=255*(ng-min)/(max-min);
        }
        for(int i=0 ; i<(w*h) ; i++) {
            int col = lut[Color.red(pixels[i])];
            pixels[i] = Color.argb(1,col,col,col);
        }
        bmp.setPixels(pixels,0,w,0,0,w,h);
    }

    //extension dynamique en couleur
    private void contrastCouleurV1(Bitmap bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int maxR = 0;
        int maxG = 0;
        int maxB = 0;
        int minR = 255;
        int minG = 255;
        int minB = 255;
        int pixels[]=new int[w*h];
        int lutR[]=new int[256];
        int lutG[]=new int[256];
        int lutB[]=new int[256];
        bmp.getPixels(pixels,0,w,0,0,w,h);
        for(int i=0 ; i<(w*h) ; i++) {
            int r = Color.red(pixels[i]);
            if(r<minR)
                minR = r;
            if(r>maxR)
                maxR = r;
            int g = Color.green(pixels[i]);
            if(g<minG)
                minG = g;
            if(g>maxG)
                maxG = g;
            int b = Color.blue(pixels[i]);
            if(b<minB)
                minB = b;
            if(b>maxB)
                maxB = b;
        }
        for(int i=0 ; i<256 ; i++) {
            lutR[i]=255*(i-minR)/(maxR-minR);
            lutG[i]=255*(i-minG)/(maxG-minG);
            lutB[i]=255*(i-minB)/(maxB-minB);
        }
        for(int i=0 ; i<(w*h) ; i++) {
            int red = lutR[Color.red(pixels[i])];
            int green = lutG[Color.green(pixels[i])];
            int blue = lutB[Color.blue(pixels[i])];
            pixels[i] = Color.argb(1,red,green,blue);
        }
        bmp.setPixels(pixels,0,w,0,0,w,h);
    }

    //calcule l'histogramme du canal "val" de l'image
    //val =0 : canal rouge
    //val =1 : canal vert
    //val =2 : canal bleu
    private int[] histogram(Bitmap bmp,int val){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int hist[] = new int[256];
        int pixels[]=new int[w*h];
        int k = 0;
        bmp.getPixels(pixels,0,w,0,0,w,h);
        for(int n=0 ; n<256 ; n++){
            hist[n]=0;
        }
        for(int i=0 ; i<(w*h) ; i++){
            if(val==0){ //val = 0 red component
                k = Color.red(pixels[i]);
            }else if(val==1){ //val = 1 green component
                k = Color.green(pixels[i]);
            }else if(val==2){ //val = 2 blue component
                k = Color.blue(pixels[i]);
            }

            hist[k] = hist[k] + 1;
        }
        return hist;
    }


    //calcule l'histogramme sur la moyenne des trois canaux
    private int[] histogramMoy(Bitmap bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int hist[] = new int[256];
        int pixels[]=new int[w*h];
        int k;
        bmp.getPixels(pixels,0,w,0,0,w,h);
        for(int n=0 ; n<256 ; n++){
            hist[n]=0;
        }
        for(int i=0 ; i<(w*h) ; i++){
            k = (Color.red(pixels[i])+Color.green(pixels[i])+Color.blue(pixels[i]))/3;
            hist[k] = hist[k] + 1;
        }
        return hist;
    }

    //égalisation d'histogramme en noir et blanc
    private void contrastV2(Bitmap bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int hist[] = histogram(bmp,0);
        int histC[] = new int[256];
        histC[0]=hist[0];
        for(int i=1 ; i<256 ; i++){
            histC[i]=histC[i-1]+hist[i];
        }
        int pixels[]=new int[w*h];
        bmp.getPixels(pixels,0,w,0,0,w,h);
        for(int i=0 ; i<(w*h) ; i++) {
            int col = (histC[Color.red(pixels[i])]*255)/(w*h);
            pixels[i] = Color.argb(1,col,col,col);
        }
        bmp.setPixels(pixels,0,w,0,0,w,h);
    }

    //égalisation d'histogramme en couleur
    private void contrastCouleurV2(Bitmap bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int hist[] = histogramMoy(bmp);
        int histC[] = new int[256];
        histC[0]=hist[0];
        for(int i=1 ; i<256 ; i++){
            histC[i]=histC[i-1]+hist[i];
        }
        int pixels[]=new int[w*h];
        bmp.getPixels(pixels,0,w,0,0,w,h);
        for(int i=0 ; i<(w*h) ; i++) {
            int red = (histC[Color.red(pixels[i])]*255)/(w*h);
            int green = (histC[Color.green(pixels[i])]*255)/(w*h);
            int blue = (histC[Color.blue(pixels[i])]*255)/(w*h);
            pixels[i] = Color.argb(1,red,green,blue);
        }
        bmp.setPixels(pixels,0,w,0,0,w,h);
    }

    //calcule l'image floutée avec un masque de taille "mask_size"
    //filtre moyenneur
    private void convolution(Bitmap bmp, int mask_size, boolean gauss){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int n = (mask_size-1)/2;
        int sumR = 0,sumG = 0, sumB=0;
        int k[] = new int[n*n];
        if(gauss){

        }
        int r,g,b;
        int pixels[]=new int[w*h];
        int pixels_new[]=new int[w*h];
        bmp.getPixels(pixels,0,w,0,0,w,h);
        for(int i=0;i<(w*h);i++){
            int y=i/w;
            int x=i%w;
            if(x>=n && y>=n && x<(w-n) && y<(h-n)) {
                for (int u = -n; u <= n; u++) {
                    for (int v = -n; v <= n; v++) {
                        int x1 = x+u;
                        int y1 = y+v;
                        int j = (y1*w)+x1;
                        sumR += Color.red(pixels[j]);
                        sumG += Color.green(pixels[j]);
                        sumB += Color.blue(pixels[j]);
                    }
                }
            }
            if(gauss){
                r=0;
                g=0;
                b=0;
            }else{
                r = sumR/(mask_size*mask_size);
                g = sumG/(mask_size*mask_size);
                b = sumB/(mask_size*mask_size);
            }
            sumR=0;
            sumG=0;
            sumB=0;
            pixels_new[i] = Color.argb(1,r,g,b);
        }
        bmp.setPixels(pixels_new,0,w,0,0,w,h);

    }

    //versions render script

    private  void  toGreyRS(Bitmap  bmp) {
        //1)  Creer un  contexte  RenderScript
        RenderScript rs = RenderScript.create(this);
        //2)  Creer  des  Allocations  pour  passer  les  donnees
        Allocation  input = Allocation.createFromBitmap(rs , bmp);
        Allocation  output = Allocation.createTyped(rs , input.getType ());
        //3)  Creer le  script
        ScriptC_grey greyScript = new ScriptC_grey(rs);
        //4)  Copier  les  donnees  dans  les  Allocations
        // ...
        //5)  Initialiser  les  variables  globales  potentielles
        // ...
        //6)  Lancer  le noyau
        greyScript.forEach_toGrey(input , output);
        //7)  Recuperer  les  donnees  des  Allocation(s)
        output.copyTo(bmp);
        //8)  Detruire  le context , les  Allocation(s) et le  script
        input.destroy (); output.destroy ();
        greyScript.destroy (); rs.destroy ();
    }

    private  void  colorizeRS(Bitmap  bmp) {
        //1)  Creer un  contexte  RenderScript
        RenderScript rs = RenderScript.create(this);
        //2)  Creer  des  Allocations  pour  passer  les  donnees
        Allocation  input = Allocation.createFromBitmap(rs , bmp);
        Allocation  output = Allocation.createTyped(rs , input.getType ());
        //3)  Creer le  script
        ScriptC_colorize colorizeScript = new ScriptC_colorize(rs);
        //4)  Copier  les  donnees  dans  les  Allocations
        // ...
        //5)  Initialiser  les  variables  globales  potentielles
        colorizeScript.set_hueValue((float)(Math.random()*360));
        //6)  Lancer  le noyau
        colorizeScript.forEach_colorize(input , output);
        //7)  Recuperer  les  donnees  des  Allocation(s)
        output.copyTo(bmp);
        //8)  Detruire  le context , les  Allocation(s) et le  script
        input.destroy (); output.destroy ();
        colorizeScript.destroy (); rs.destroy ();
    }

    public void equalizationHistogramRS(Bitmap bmp){
        int width = bmp.getWidth();
        int height = bmp.getHeight();

        //1)  Creer un  contexte  RenderScript
        RenderScript rs = RenderScript.create(this);
        //2)  Creer  des  Allocations  pour  passer  les  donnees
        Allocation  input = Allocation.createFromBitmap(rs , bmp);
        Allocation  output = Allocation.createTyped(rs , input.getType ());
        //3)  Creer le  script
        ScriptC_histEq histEqScript = new ScriptC_histEq(rs);
        //4)  Copier  les  donnees  dans  les  Allocations
        // ...
        //5)  Initialiser  les  variables  globales  potentielles
        histEqScript.set_size(width*height);
        //6)  Lancer  le noyau
        histEqScript.forEach_root(input, output);
        histEqScript.invoke_createRemapArray();
        histEqScript.forEach_remaptoRGB(output, input);
        //7)  Recuperer  les  donnees  des  Allocation(s)
        input.copyTo(bmp);
        //8)  Detruire  le context , les  Allocation(s) et le  script
        input.destroy (); output.destroy ();
        histEqScript.destroy();
        rs.destroy();

    }

    private  void  keepRedRS(Bitmap  bmp) {
        //1)  Creer un  contexte  RenderScript
        RenderScript rs = RenderScript.create(this);
        //2)  Creer  des  Allocations  pour  passer  les  donnees
        Allocation  input = Allocation.createFromBitmap(rs , bmp);
        Allocation  output = Allocation.createTyped(rs , input.getType ());
        //3)  Creer le  script
        ScriptC_keepred keepredScript = new ScriptC_keepred(rs);
        //4)  Copier  les  donnees  dans  les  Allocations
        // ...
        //5)  Initialiser  les  variables  globales  potentielles
        //6)  Lancer  le noyau
        keepredScript.forEach_keepred(input , output);
        //7)  Recuperer  les  donnees  des  Allocation(s)
        output.copyTo(bmp);
        //8)  Detruire  le context , les  Allocation(s) et le  script
        input.destroy (); output.destroy ();
        keepredScript.destroy (); rs.destroy ();
    }



}
