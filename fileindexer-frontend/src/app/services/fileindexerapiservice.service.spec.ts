import { TestBed } from '@angular/core/testing';

import { FileIndexerApiserviceService } from './fileindexerapiservice.service';

describe('FileIndexerApiserviceService', () => {
  let service: FileIndexerApiserviceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FileIndexerApiserviceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
