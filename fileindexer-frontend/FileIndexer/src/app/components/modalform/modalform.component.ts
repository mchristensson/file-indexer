import { Component, ElementRef, Input, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { ModalformService } from '../../services/modalform.service';

@Component({
  selector: 'app-modalform',
  templateUrl: './modalform.component.html',
  styleUrls: ['./modalform.component.css']
})
export class ModalformComponent implements OnInit, OnDestroy {

  @Input() id?: string;
  isRevealed: boolean = false;
  private formElement: any;

  constructor(private modalformService: ModalformService , formElementRef: ElementRef) {
    this.formElement = formElementRef.nativeElement;
  }

  ngOnInit() {
    this.modalformService.add(this);
    document.body.appendChild(this.formElement);
  }

  ngOnDestroy(): void {
    this.modalformService.remove(this);
    this.formElement.remove();
  }

  close() {
    this.formElement.style.display = 'none';
    this.isRevealed = false;
  }

  reveal() {
    this.formElement.style.display = 'block';
    this.isRevealed = true;
  }

}
