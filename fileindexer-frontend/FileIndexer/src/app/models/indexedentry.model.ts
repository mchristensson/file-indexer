export class Indexedentry {
}

export class ScanJobStatusDataEntry {
    id:string;
    status:number;
    errorMessage:string;
}



export class ImgHashData {
    id: string;
    deviceId: string;
    devicePath: string;
    checksum: string;
    scanTime: number;
    date: Date;
    properties: any;
    
}

export class EnqueuedTask  {
    jobTitle: string;
    settings: EnqueuedTaskInstruction;
}

export class EnqueuedTaskInstruction  {
    devicePath: string;
    urlType: string;
    deviceId: string;
}

export class ImageTransformInstruction {
    imageId: string;
    imageWidth: number;
    imageHeight : number;
}

export class EqueueJobReceipt {
    id: number; 
    errorMessage: string; 
    message: string;
}

export class ScannedDataEntry {
    id: string;
    date: Date;
    devicePath: string;
    deviceId: string;
    scanTime: number;
/*
date
: 
"2023-04-26T13:43:06.730Z"
deviceId
: 
"55912ca2-910f-485f-822e-5b258314cedc"
devicePath
: 
"//opt/app/test-filestructure/copyrighted/catsanddogs/test/dogs/dog_196.jpg"
id
: 
"d9e091ec-938e-429f-9a6d-6578161ea28f"
properties
: 
{Date/Time: '2022-12-20T10:13:04Z', File Name: 'dog_196.jpg', File Size: '43353', Image-height: '400', Image-width: '650', …}
scanTime
: 
0
*/
}

export class LogicalDevice {
    title: string;
    id: string;
}
