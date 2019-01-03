#pragma  version (1)
#pragma  rs  java_package_name(com.example.achauveau003.appimage)


uchar4  RS_KERNEL  keepred(uchar4  in) {
    float4  pixelf = rsUnpackColor8888(in);

    //conversion rgb to hsv
    float r = pixelf.r;
    float g = pixelf.g;
    float b = pixelf.b;
    float Cmax = max(r,max(g,b));
    float Cmin = min(r,min(g,b));
    float h;
    if (Cmax == Cmin){
        h = 0;
    }else if(Cmax == r){
        h = (int)(60*(g-b)/(Cmax-Cmin)+360)%360;
    }else if(Cmax == g){
        h = (int)60*(b-r)/(Cmax-Cmin)+120;
    }else if(Cmax == b){
        h = (int)60*(r-g)/(Cmax-Cmin)+240;
    }

    if (h>20 && h<340){      //hue is not red : turn to grey
        float  grey = (0.30*r + 0.59*g + 0.11*b);
        return  rsPackColorTo8888(grey , grey , grey , pixelf.a);
    }else{
        return  rsPackColorTo8888(r , g , b , pixelf.a);
    }

}