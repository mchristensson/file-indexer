import { Component } from '@angular/core';
import { DefaultapiserviceService } from '../../services/defaultapiservice.service';
import { scan , Subscription, switchMap, startWith , interval } from 'rxjs';
import { ImgHashData } from 'src/app/models/indexedentry.model';

@Component({
  selector: 'app-imgash-list',
  templateUrl: './imgash-list.component.html',
  styleUrls: ['./imgash-list.component.css']
})
export class ImgashListComponent {
  
  constructor(private apiService: DefaultapiserviceService) {}
  
  imgHashData: ImgHashData[];
  imgHashDataSubscriptionTi: Subscription;
  selectedImage: any;

  ngOnInit() {
    this.imgHashDataSubscriptionTi = interval(5000).pipe(
      startWith(0),
      switchMap(() => this.apiService.getImgHashDataList())
    ).subscribe( result => {
      console.log("Handling result...", result);
      this.imgHashData = result.values;
    } )

  }

  ngOnDestroy() {
    this.imgHashDataSubscriptionTi.unsubscribe();
  }

  fetchImage(imageId) {
    this.selectedImage = null;
    console.log("Fetching image... ", imageId);
    this.apiService.getSmallImage(imageId).subscribe(result => {
      console.log("Fetched image: ", result);
      this.selectedImage = result;
    });

    /*
    http://localhost:8080/api/imgash/image?id=e86e03b8-73bf-46da-a651-e8cb7760ae16
    */
  }

}
