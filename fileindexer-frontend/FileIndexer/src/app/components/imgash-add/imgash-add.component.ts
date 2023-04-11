import { Component } from '@angular/core';
import { DefaultapiserviceService } from '../../services/defaultapiservice.service';
import { FormBuilder, FormGroup } from '@angular/forms';
import { scan } from 'rxjs';

@Component({
  selector: 'app-imgash-add',
  templateUrl: './imgash-add.component.html',
  styleUrls: ['./imgash-add.component.css']
})
export class ImgashAddComponent {

  constructor(private apiService: DefaultapiserviceService, private formBuilder: FormBuilder) {}
  
  urlTypes = ["UNIX",  "WIN"];
  deviceIds = [ "7f800e14-47f0-4ca3-8010-499bd70cd569"];

  createImageAnalysisJobForm = this.formBuilder.group({
    path: '',
    type: '',
    deviceId: ''
  });


  enqueueImageAnalysisJob(): void {
    console.log("Skickar data... ", this.createImageAnalysisJobForm.value);
    this.apiService.imgAnalysisEnqueue(this.createImageAnalysisJobForm.value)
    .subscribe(scanEnqueueReceipt => {
      console.log("Result ", scanEnqueueReceipt)
      //this.createImageAnalysisJobForm.reset();
    });
    
  }
}
