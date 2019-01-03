#pragma  version (1)
#pragma  rs  java_package_name(com.example.achauveau003.appimage)

float hueValue = 1.0;

uchar4  RS_KERNEL  colorize(uchar4  in) {
    float4  pixelf = rsUnpackColor8888(in);

    //conversion rgb to hsv
    float r = pixelf.r;
    float g = pixelf.g;
    float b = pixelf.b;
    float Cmax = max(r,max(g,b));
    float Cmin = min(r,min(g,b));
    float h = hueValue;
    float s;
    if (Cmax == 0){
        s = 0;
    }else{
        s = 1-(Cmin/Cmax);
    }
    float v = Cmax;

    //conversion hsv to rgb
    float t = (int)(h/60)%6;
    float f = (h/60)-t;
    float l = v*(1-s);
    float m = v*(1-f*s);
    float n = v*(1-(1-f)*s);

    if (t==0){
        return  rsPackColorTo8888(v,n,l, pixelf.a);
    }else if (t==1){
        return  rsPackColorTo8888(m,v,l, pixelf.a);
    }else if (t==2){
        return  rsPackColorTo8888(l,v,n, pixelf.a);
    }else if (t==3){
        return  rsPackColorTo8888(l,m,v, pixelf.a);
    }else if (t==4){
        return  rsPackColorTo8888(n,l,v, pixelf.a);
    }else if (t==5){
        return  rsPackColorTo8888(v,l,m, pixelf.a);
    }
}