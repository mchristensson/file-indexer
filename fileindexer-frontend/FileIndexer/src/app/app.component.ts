import { Component } from '@angular/core';
import { ModalformService } from './services/modalform.service';
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'FileIndexer';

  constructor(protected modalformService: ModalformService) {}
}
