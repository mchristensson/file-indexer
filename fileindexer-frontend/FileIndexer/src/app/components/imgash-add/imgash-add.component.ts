import { Component } from '@angular/core';
import { DefaultapiserviceService } from '../../services/defaultapiservice.service';
import { FormBuilder } from '@angular/forms';
import { EnqueuedTask } from 'src/app/models/indexedentry.model';

@Component({
  selector: 'app-imgash-add',
  templateUrl: './imgash-add.component.html',
  styleUrls: ['../common-form-styles.css']
})
export class ImgashAddComponent {

  constructor(private apiService: DefaultapiserviceService, private formBuilder: FormBuilder) {}
  
  urlTypes = ["UNIX",  "WIN"];
  deviceIds = [ "7f800e14-47f0-4ca3-8010-499bd70cd569"];

  createImageAnalysisJobForm = this.formBuilder.group({
    taskTitle: 'foo',
    path: '',
    type: '',
    deviceId: ''
  });

  enqueueImageAnalysisJob(): void {
    console.log("Skickar data... ", this.createImageAnalysisJobForm.value);
    var arg = new EnqueuedTask();
    arg.jobTitle = this.createImageAnalysisJobForm.value.taskTitle;
    this.apiService.taskEnqueue(arg)
    .subscribe(scanEnqueueReceipt => {
      this.createImageAnalysisJobForm.reset();
    });
    
  }

}
