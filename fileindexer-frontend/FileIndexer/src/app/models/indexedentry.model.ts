export class Indexedentry {
}

export class ScanJobStatusDataEntry {
    id:string;
    status:number;
}

export class ScanJobStatusData  {
    data = new Map<string, number>();
    timestamp: Date
}


