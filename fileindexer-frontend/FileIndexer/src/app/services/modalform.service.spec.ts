import { TestBed } from '@angular/core/testing';

import { ModalformService } from './modalform.service';

describe('ModalformService', () => {
  let service: ModalformService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ModalformService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
