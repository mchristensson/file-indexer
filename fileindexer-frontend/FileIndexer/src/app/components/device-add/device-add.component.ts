import { Component, ViewEncapsulation } from '@angular/core';
import { DefaultapiserviceService } from '../../services/defaultapiservice.service';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ModalformService } from 'src/app/services/modalform.service';

@Component({
  selector: 'app-device-add',
  templateUrl: './device-add.component.html',
  styleUrls: ['../common-form-styles.css']
})
export class DeviceAddComponent {

  constructor(private apiService: DefaultapiserviceService, private formBuilder: FormBuilder, 
    private modalformService: ModalformService) {}
  
  createDeviceForm = this.formBuilder.group({
    title: '',
    devicePath: ''
  });

  formErrorMessage: string;

  createDevice(): void {
    this.formErrorMessage = null;
    this.apiService.createDevice(this.createDeviceForm.value)
    .subscribe({
      next: (v) => {},
      error: (e) => {
        this.formErrorMessage = e.message;
      },
      complete: () => {
        this.modalformService.close();
      }
    });

  }

  cancel(): void {
    this.modalformService.close();
  }

}
