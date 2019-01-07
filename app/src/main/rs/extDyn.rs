#pragma  version (1)
#pragma  rs  java_package_name(com.example.achauveau003.appimage)

int valMin=255;
int valMax=0;
int lut[256];

void createLut(int valMin, int valMax){
    int lut[256];
    for(int ng=0 ; ng<256 ; ng++) {
        lut[ng]=255*(ng-valMin)/(valMax-valMin);
    }
}

void RS_KERNEL min_max(uchar4 in){
    float4  pixelf = rsUnpackColor8888(in);
    float r = pixelf.r;
    float g = pixelf.g;
    float b = pixelf.b;
    int col = (int)(r+g+b)/3;
    if(col<valMin){
        valMin=col;
    }else if(col>valMax){
        valMax=col;
    }
}

uchar4  RS_KERNEL  extDyn(uchar4  in) {
    float4  pixelf = rsUnpackColor8888(in);
    float r = pixelf.r;
    float g = pixelf.g;
    float b = pixelf.b;
    int col = (r+g+b)/3;
    int new_col = lut[col];

    return  rsPackColorTo8888(new_col,new_col,new_col, pixelf.a);

}