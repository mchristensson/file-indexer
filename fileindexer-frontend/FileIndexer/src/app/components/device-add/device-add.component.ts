import { Component } from '@angular/core';
import { DefaultapiserviceService } from '../../services/defaultapiservice.service';
import { FormBuilder, FormGroup } from '@angular/forms';

@Component({
  selector: 'app-device-add',
  templateUrl: './device-add.component.html',
  styleUrls: ['./device-add.component.css']
})
export class DeviceAddComponent {

  constructor(private apiService: DefaultapiserviceService, private formBuilder: FormBuilder) {}
  
  createDeviceForm = this.formBuilder.group({
    title: '',
    devicePath: ''
  });

  createDevice(): void {
    console.log("Submitting data... ", this.createDeviceForm.value);
    this.apiService.createDevice(this.createDeviceForm.value)
    .subscribe({
      next: (v) => console.log(v),
      error: (e) => console.error(e),
      complete: () => console.info('complete')
    });

  }
}
