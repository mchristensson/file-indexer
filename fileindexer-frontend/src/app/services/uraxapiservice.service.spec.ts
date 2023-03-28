import { TestBed } from '@angular/core/testing';

import { UraxapiserviceService } from './uraxapiservice.service';

describe('UraxapiserviceService', () => {
  let service: UraxapiserviceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UraxapiserviceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
