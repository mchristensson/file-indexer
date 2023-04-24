import { Component } from '@angular/core';
import { DefaultapiserviceService } from '../../services/defaultapiservice.service';
import { FormBuilder, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-scanjob-add',
  templateUrl: './scanjob-add.component.html',
  styleUrls: ['./scanjob-add.component.css']
})
export class ScanjobAddComponent {

  constructor(private apiService: DefaultapiserviceService, private formBuilder: FormBuilder) {}
  
  urlTypes = ["UNIX",  "WIN"];
  deviceIds: any[];

  createScanJobForm = this.formBuilder.group({
    path: '',
    type: '',
    deviceId: ''
  });

  ngOnInit() {
    console.log("Fetching devices... ");
    this.apiService.getDeviceList()
    .subscribe(deviceList => {
      console.log("Devices... ", deviceList);
      this.deviceIds = deviceList;
    });
  }
  
  enqueueScanJob(): void {
    console.log("Transferring scan-job data... ", this.createScanJobForm.value);
    this.apiService.scanEnqueue(this.createScanJobForm.value)
    .subscribe(scanEnqueueReceipt => {
      this.createScanJobForm.reset();
    });  
  }

}
