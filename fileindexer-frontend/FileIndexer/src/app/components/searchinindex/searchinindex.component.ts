import { Component } from '@angular/core';
import { interval, startWith, Subscription, switchMap, timeInterval } from 'rxjs';
import { ScannedDataEntry, EnqueuedTask, EnqueuedTaskInstruction, ImageTransformInstruction } from 'src/app/models/indexedentry.model';
import { DefaultapiserviceService } from '../../services/defaultapiservice.service';

@Component({
  selector: 'app-searchinindex',
  templateUrl: './searchinindex.component.html',
  styleUrls: ['./searchinindex.component.css', '../common-list-styles.css']
})
export class SearchinindexComponent {

  constructor(private apiService: DefaultapiserviceService) {}
  scannedData: ScannedDataEntry[];
  scannedDataSubscriptionTi: Subscription;
  selectedElement: ScannedDataEntry;

  ngOnInit() {
    this.scannedDataSubscriptionTi = interval(5000).pipe(
      startWith(0),
      switchMap(() => this.apiService.getScanData())
    ).subscribe( result => {
      console.log("Handling result (getScanData)...", result);
      this.scannedData = result.values;
    } )

  }

  ngOnDestroy() {
    this.scannedDataSubscriptionTi.unsubscribe();
  }

  setSelected(element: any) {
    this.selectedElement = element;
  }

  createAnalysisTask(): void {
    var arg = new EnqueuedTask();
    arg.jobTitle = "Image File Hash Generator";
    var settings = new EnqueuedTaskInstruction();
    settings.deviceId = this.selectedElement.deviceId;
    settings.urlType = "UNIX";
    settings.devicePath = this.selectedElement.devicePath;
    //settings.devicePath = 'opt/app/test-filestructure/copyrighted/catsanddogs/train/dogs/dog_333.jpg';
    arg.settings = settings;

    console.log("Transferring task data... ", arg);
    this.apiService.taskEnqueue(arg)
    .subscribe(scanEnqueueReceipt => {
      console.log("Result: ", scanEnqueueReceipt)
    });  
  }

}
