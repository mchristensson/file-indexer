import { Component } from '@angular/core';
import { UraxapiserviceService } from '../../services/fileindexerapiservice.service';

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.css']
})
export class ProductListComponent {
  
  products:any

  constructor(private apiService: UraxapiserviceService) {}

  ngOnInit() {
    console.log("ngoninit fetch from service...")
    this.apiService.getProducts().subscribe(productData => {
      this.products = productData;
      console.log("Produtcs: ", this.products)
    });
  }

}
