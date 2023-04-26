import { Component } from '@angular/core';
import { DefaultapiserviceService } from '../../services/defaultapiservice.service';
import { FormBuilder, FormGroup } from '@angular/forms';
import { EnqueuedTask, EnqueuedTaskInstruction } from 'src/app/models/indexedentry.model';

@Component({
  selector: 'app-scanjob-add',
  templateUrl: './scanjob-add.component.html'
})
export class ScanjobAddComponent {

  constructor(private apiService: DefaultapiserviceService, private formBuilder: FormBuilder) {}
  
  urlTypes = ["UNIX",  "WIN"];
  deviceIds: any[];
  taskList: String[];

  createScanJobForm = this.formBuilder.group({
    taskTitle: '',
    devicePath: '',
    urlType: '',
    deviceId: ''
  });

  ngOnInit() {
    console.log("Fetching data... ");
    this.apiService.getDeviceList()
    .subscribe(deviceList => {
      this.deviceIds = deviceList;
    });

    this.apiService.getTaskList()
    .subscribe(tasks => {
      this.taskList = tasks;
    });
  }
  
  enqueueScanJob(): void {
    var arg = new EnqueuedTask();
    arg.jobTitle = this.createScanJobForm.value.taskTitle;
    var settings = new EnqueuedTaskInstruction();
    settings.deviceId = this.createScanJobForm.value.deviceId;
    settings.urlType = this.createScanJobForm.value.urlType;
    settings.devicePath = this.createScanJobForm.value.devicePath;
    arg.settings = settings;

    console.log("Transferring task data... ", arg);
    this.apiService.taskEnqueue(arg)
    .subscribe(scanEnqueueReceipt => {
      console.log("Result: ", scanEnqueueReceipt)
      this.createScanJobForm.reset();
    });  
  }

}
