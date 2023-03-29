import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchinindexComponent } from './searchinindex.component';

describe('SearchinindexComponent', () => {
  let component: SearchinindexComponent;
  let fixture: ComponentFixture<SearchinindexComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SearchinindexComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SearchinindexComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
