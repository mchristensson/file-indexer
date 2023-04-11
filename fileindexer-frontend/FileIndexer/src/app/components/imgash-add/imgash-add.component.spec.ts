import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ImgashAddComponent } from './imgash-add.component';

describe('ImgashAddComponent', () => {
  let component: ImgashAddComponent;
  let fixture: ComponentFixture<ImgashAddComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ImgashAddComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ImgashAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
