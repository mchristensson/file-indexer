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
  deviceIds = [ "7f800e14-47f0-4ca3-8010-499bd70cd569"];

  createScanJobForm = this.formBuilder.group({
    path: '',
    type: '',
    deviceId: ''
  });


  enqueueScanJob(): void {
    console.log("Skickar data... ", this.createScanJobForm.value);
    this.apiService.scanEnqueue(this.createScanJobForm.value)
    .subscribe(scanEnqueueReceipt => {
      this.createScanJobForm.reset();
    });
    
  }
}
