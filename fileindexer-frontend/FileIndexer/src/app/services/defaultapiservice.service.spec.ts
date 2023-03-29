import { TestBed } from '@angular/core/testing';

import { DefaultapiserviceService } from './defaultapiservice.service';

describe('DefaultapiserviceService', () => {
  let service: DefaultapiserviceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DefaultapiserviceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
