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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.widget.Spinner;
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
    private int num_image=0;

    //changeImage : change the current image
    private void changeImage(int num_image){
        img = findViewById(R.id.picture);
        options = new BitmapFactory.Options();
        options.inMutable=true;
        options.inScaled=false;
        switch (num_image){
            case 0:
                bmap = BitmapFactory.decodeResource(getResources(),R.drawable.noirblanc,options);
                break;
            case 1:
                bmap = BitmapFactory.decodeResource(getResources(),R.drawable.noiretblanc,options);
                break;
            case 2:
                bmap = BitmapFactory.decodeResource(getResources(),R.drawable.couleur,options);
                break;
            case 3:
                bmap = BitmapFactory.decodeResource(getResources(),R.drawable.couleur1,options);
                break;
            default:
                break;
        }
        bmap_copy = bmap.copy(Bitmap.Config.ARGB_8888,true);
        img.setImageBitmap(bmap);
    }

    Spinner spin;
    ArrayAdapter<String> adapter;
    String[] methods={"toGrey","toGreyRS","colorize","colorizeRS","keepRed","keepRedRS","contrastExtDyn","contrastExtDynRS","contrastHistEq","contrastHistEqRS","blur3","blur5","blur11","contourPrewitt","contourSobel"};
    //select : current effect selected
    String select="toGrey";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spin=findViewById(R.id.spinner);
        adapter= new ArrayAdapter(this,android.R.layout.simple_list_item_1,methods);
        spin.setAdapter(adapter);

        changeImage(num_image);

        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                select=methods[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //initialization of the 2 masks for prewitt
        final int[][] prewittX = new int[3][3];
        final int[][] prewittY = new int[3][3];
        for(int x=0;x<3;x++){
            for(int y=0;y<3;y++){
                if(x==0){
                    prewittX[x][y]=-1;
                }else if(x==2){
                    prewittX[x][y]=1;
                }else{
                    prewittX[x][y]=0;
                }
                if(y==0){
                    prewittY[x][y]=-1;
                }else if(y==2){
                    prewittY[x][y]=1;
                }else{
                    prewittY[x][y]=0;
                }
            }
        }

        //initialization of the 2 masks for sobel
        final int[][] sobelX = new int[3][3];
        final int[][] sobelY = new int[3][3];
        for(int x=0;x<3;x++){
            for(int y=0;y<3;y++){
                if(x==0 && y!=1) {
                    sobelX[x][y] = -1;
                }else if(x==0 && y==1){
                    sobelX[x][y] = -2;
                }else if(x==2 && y!=1) {
                    sobelX[x][y] = 1;
                }else if(x==2 && y==1){
                    sobelX[x][y] = 2;
                }else{
                    sobelX[x][y]=0;
                }
                if(y==0 && x!=1) {
                    sobelY[x][y] = -1;
                }else if(y==0 && x==1){
                    sobelY[x][y] = -2;
                }else if(y==2 && x!=1) {
                    sobelY[x][y] = 1;
                }else if(y==2 && x==1){
                    sobelY[x][y] = 2;
                }else{
                    sobelY[x][y]=0;
                }
            }
        }

        //button DO : apply the selected effect
        button = findViewById(R.id.button_id);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                switch(select){
                    case "toGrey":
                        toGreys(bmap);
                        break;
                    case "toGreyRS":
                        toGreyRS(bmap);
                        break;
                    case "colorize":
                        colorize(bmap);
                        break;
                    case "colorizeRS":
                        colorizeRS(bmap);
                        break;
                    case "keepRed":
                        keepRed(bmap);
                        break;
                    case "keepRedRS":
                        keepRedRS(bmap);
                        break;
                    case "contrastExtDyn":
                        contrastExtDyn(bmap);
                        break;
                    case "contrastExtDynRS":
                        contrastExtDynRS(bmap);
                        break;
                    case "contrastHistEq":
                        contrastHistEq(bmap);
                        break;
                    case "contrastHistEqRS":
                        contrastHistEqRS(bmap);
                        break;
                    case "blur3":
                        convolutionBlur(bmap,3);
                        break;
                    case "blur5":
                        convolutionBlur(bmap,5);
                        break;
                    case "blur11":
                        convolutionBlur(bmap,11);
                        break;
                    case "contourPrewitt":
                        convolutionContour(bmap,prewittX,prewittY);
                        break;
                    case "contourSobel":
                        convolutionContour(bmap,sobelX,sobelY);
                        break;
                    default:
                        break;

                }
            }
        });

        //button RESET : put the original image back
        button2 = findViewById(R.id.button2_id);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                reset();
            }
        });

        //button NEXT IMAGE : change the current image
        button3 = findViewById(R.id.button3_id);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(num_image==3)num_image=0;
                else num_image++;
                changeImage(num_image);
            }
        });


    }




    private void reset(){
        bmap = bmap_copy.copy(Bitmap.Config.ARGB_8888,true);
        img.setImageBitmap(bmap);
    }

    //put the picture in grey pixel by pixel
    //this method is not used
    private void toGrey(Bitmap bmp){
        int col ;
        for(int i=0;i<bmp.getWidth();i++){
            for(int j=0;j<bmp.getHeight();j++){
                col=bmp.getPixel(i,j);
                int r = Color.red(col);
                int g = Color.green(col);
                int b = Color.blue(col);
                double new_col = 0.3*r+0.59*g+0.11*b;
                bmp.setPixel(i,j,Color.argb(1,(int)new_col,(int)new_col,(int)new_col));
            }
        }
    }

    //put the picture in grey
    private void toGreys(Bitmap bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int size =w*h;
        int pixels[]=new int[size];
        bmp.getPixels(pixels,0,w,0,0,w,h);
        for(int i=0 ;i<size;i++){
            int r = Color.red(pixels[i]);
            int g = Color.green(pixels[i]);
            int b = Color.blue(pixels[i]);
            double new_col = 0.3*r+0.59*g+0.11*b;
            pixels[i]=Color.argb(1,(int)new_col,(int)new_col,(int)new_col);
        }
        bmp.setPixels(pixels,0,w,0,0,w,h);


    }

    //colorize the picture with a random hue
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

    //keep the red part of the picture and change the rest in grey
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
            //on the chromatic circle the red color is between 340 degrees and 20 degrees
            if (hsv[0] > 20 && hsv[0] < 340) {
                double new_col = 0.3 * r + 0.59 * g + 0.11 * b;
                pixels[i] = Color.argb(1, (int) new_col, (int) new_col, (int) new_col);
            }
        }
        bmp.setPixels(pixels,0,w,0,0,w,h);
    }

    //contrast with dynamic extension
    private void contrastExtDyn(Bitmap bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();

        //initialization of the mins and maxs
        int maxR = 0;
        int maxG = 0;
        int maxB = 0;
        int minR = 255;
        int minG = 255;
        int minB = 255;

        int pixels[]=new int[w*h];

        //initialization of the 3 lut (look up table) for the 3 components
        int lutR[]=new int[256];
        int lutG[]=new int[256];
        int lutB[]=new int[256];

        bmp.getPixels(pixels,0,w,0,0,w,h);

        //calculate the mins and maxs
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

        //calculate the values in the 3 lut
        for(int i=0 ; i<256 ; i++) {
            lutR[i]=255*(i-minR)/(maxR-minR);
            lutG[i]=255*(i-minG)/(maxG-minG);
            lutB[i]=255*(i-minB)/(maxB-minB);
        }

        //calculate the new color depending on the value in the lut
        for(int i=0 ; i<(w*h) ; i++) {
            int red = lutR[Color.red(pixels[i])];
            int green = lutG[Color.green(pixels[i])];
            int blue = lutB[Color.blue(pixels[i])];
            pixels[i] = Color.argb(1,red,green,blue);
        }

        bmp.setPixels(pixels,0,w,0,0,w,h);
    }

    //calculate the histogram with the average between the 3 components
    private int[] histogramAv(Bitmap bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int hist[] = new int[256];
        int pixels[]=new int[w*h];
        int average;
        bmp.getPixels(pixels,0,w,0,0,w,h);

        //initialization of the histogram
        for(int n=0 ; n<256 ; n++){
            hist[n]=0;
        }

        //calculate the histogram
        for(int i=0 ; i<(w*h) ; i++){
            average = (Color.red(pixels[i])+Color.green(pixels[i])+Color.blue(pixels[i]))/3;
            hist[average] = hist[average] + 1;
        }

        return hist;
    }

    //contrast with histogram equalization
    private void contrastHistEq(Bitmap bmp){
        int w = bmp.getWidth();
        int h = bmp.getHeight();

        //get the histogram
        int hist[] = histogramAv(bmp);

        //histC : cumulative histogram
        int histC[] = new int[256];
        histC[0]=hist[0];
        for(int i=1 ; i<256 ; i++){
            histC[i]=histC[i-1]+hist[i];
        }

        int pixels[]=new int[w*h];
        bmp.getPixels(pixels,0,w,0,0,w,h);

        //calculate the new color depending on the cumulative histogram
        for(int i=0 ; i<(w*h) ; i++) {
            int red = (histC[Color.red(pixels[i])]*255)/(w*h);
            int green = (histC[Color.green(pixels[i])]*255)/(w*h);
            int blue = (histC[Color.blue(pixels[i])]*255)/(w*h);
            pixels[i] = Color.argb(1,red,green,blue);
        }

        bmp.setPixels(pixels,0,w,0,0,w,h);
    }

    //blur the image by convolution with a mask of size "mask_size"
    private void convolutionBlur(Bitmap bmp, int mask_size){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        //n:number of paces needed to go from the pixel in the center to the edge
        int n = (mask_size-1)/2;
        int sumR = 0,sumG = 0, sumB=0;
        int r,g,b;
        int pixels[]=new int[w*h];
        //pixels_new permit to calculate the image without using values that have already been changed
        int pixels_new[]=new int[w*h];

        bmp.getPixels(pixels,0,w,0,0,w,h);
        for(int i=0;i<(w*h);i++){
            int y=i/w;
            int x=i%w;
            //check if the pixel is not on the edge of the picture
            if(x>=n && y>=n && x<(w-n) && y<(h-n)) {
                //go through the pixels around
                for (int u = -n; u <= n; u++) {
                    for (int v = -n; v <= n; v++) {
                        int x1 = x+u;
                        int y1 = y+v;
                        //ind : index of the pixel going through the mask
                        int ind = (y1*w)+x1;
                        sumR += Color.red(pixels[ind]);
                        sumG += Color.green(pixels[ind]);
                        sumB += Color.blue(pixels[ind]);
                    }
                }
            }

            //calculate the new color
            r = sumR/(mask_size*mask_size);
            g = sumG/(mask_size*mask_size);
            b = sumB/(mask_size*mask_size);

            sumR=0;
            sumG=0;
            sumB=0;

            pixels_new[i] = Color.argb(1,r,g,b);
        }
        bmp.setPixels(pixels_new,0,w,0,0,w,h);

    }

    //accentuate the contours in the image
    private void convolutionContour(Bitmap bmp, int[][] kernelX, int [][] kernelY){
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int pixels[]=new int[w*h];
        //pixels_new permit to calculate the image without using values that have already been changed
        int pixels_new[]=new int[w*h];
        bmp.getPixels(pixels,0,w,0,0,w,h);

        //gx : gradient x
        //gy : gradient y
        int gx=0,gy=0;

        for(int i=0;i<(w*h);i++) {
            int y = i / w;
            int x = i % w;
            if(x>=1 && y>=1 && x<(w-1) && y<(h-1)) {
                gx = 0;
                gy = 0;
                //go through the pixels around
                for (int u = -1; u <= 1; u++) {
                    for (int v = -1; v <= 1; v++) {
                        int x1 = x + u;
                        int y1 = y + v;
                        //ind : index of the pixel going through the mask
                        int ind = (y1 * w) + x1;

                        //get the color
                        int r = Color.red(pixels[ind]);
                        int g = Color.green(pixels[ind]);
                        int b = Color.blue(pixels[ind]);
                        int col = (r+g+b)/3;

                        //apply the masks
                        gx = gx + col * kernelX[u + 1][v + 1];
                        gy = gy + col * kernelY[u + 1][v + 1];

                    }
                }
            }

            //mod:module of the gradient
            int mod = (int) Math.sqrt(gx * gx + gy * gy);
            if (mod > 255) mod = 255;

            pixels_new[i] = Color.argb(i, mod, mod, mod);

        }
        bmp.setPixels(pixels_new,0,w,0,0,w,h);
    }

    //renderscript versions

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

    public void contrastHistEqRS(Bitmap bmp){
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

    private  void  contrastExtDynRS(Bitmap  bmp) {
        //1)  Creer un  contexte  RenderScript
        RenderScript rs = RenderScript.create(this);
        //2)  Creer  des  Allocations  pour  passer  les  donnees
        Allocation  input = Allocation.createFromBitmap(rs , bmp);
        Allocation  output = Allocation.createTyped(rs , input.getType ());
        //3)  Creer le  script
        ScriptC_extDyn extDynScript = new ScriptC_extDyn(rs);
        //4)  Copier  les  donnees  dans  les  Allocations
        // ...
        //5)  Initialiser  les  variables  globales  potentielles

        //6)  Lancer  le noyau
        int[] minMax={255,0};
        extDynScript.set_valMin(255);
        extDynScript.set_valMax(0);
        extDynScript.forEach_min_max(input);
        minMax[0]=extDynScript.get_valMin();
        minMax[1]=extDynScript.get_valMax();
        extDynScript.invoke_createLut(minMax[0],minMax[1]);
        extDynScript.forEach_extDyn(input , output);
        //7)  Recuperer  les  donnees  des  Allocation(s)
        output.copyTo(bmp);
        //8)  Detruire  le context , les  Allocation(s) et le  script
        input.destroy (); output.destroy ();
        extDynScript.destroy (); rs.destroy ();
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
