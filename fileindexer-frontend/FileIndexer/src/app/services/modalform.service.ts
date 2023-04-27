import { Injectable } from '@angular/core';
import { ModalformComponent } from '../components/modalform/modalform.component';

@Injectable({
  providedIn: 'root'
})
export class ModalformService {

  private modalforms: ModalformComponent[] = [];

  constructor() { }

  add(component: ModalformComponent): void {
    if (this.modalforms.find(elem => elem.id === component.id)) {
      throw new Error('Invalid form to add');
    }
    this.modalforms.push(component);
  }

  remove(component: ModalformComponent): void {
    console.log("n-elements before remove: ",this.modalforms.length);
    this.modalforms = this.modalforms.filter(elem => elem === component);
    console.log("n-elements after remove: ",this.modalforms.length);
  }
  
  reveal(id: string): void {
    const modalForm = this.modalforms.find(elem => elem.id === id);
    console.log("form: ",modalForm);
    modalForm?.reveal();
  }

  close(): void {
    const modal = this.modalforms.find(elem => elem.isRevealed);
    modal?.close();
  }
}
