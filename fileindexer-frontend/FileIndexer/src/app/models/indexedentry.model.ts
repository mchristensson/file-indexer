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
/*
export class ScanJobStatusData  {
    data = new Map<string, number>();
    timestamp: Date
}
*/

export class EqueueJobReceipt {
    id: number; 
    errorMessage: string; 
    message: string;
}

export class ScannedDataEntry {
    name:string;
}