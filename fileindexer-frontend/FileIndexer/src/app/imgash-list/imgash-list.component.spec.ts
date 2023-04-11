import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImgashListComponent } from './imgash-list.component';

describe('ImgashListComponent', () => {
  let component: ImgashListComponent;
  let fixture: ComponentFixture<ImgashListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ImgashListComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ImgashListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
