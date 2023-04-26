import { Component } from '@angular/core';
import { DefaultapiserviceService } from '../../services/defaultapiservice.service';
import { scan , Subscription, switchMap, startWith , interval } from 'rxjs';
import { ImgHashData, ImageTransformInstruction } from 'src/app/models/indexedentry.model';

@Component({
  selector: 'app-imgash-list',
  templateUrl: './imgash-list.component.html',
  styleUrls: ['./imgash-list.component.css', '../common-list-styles.css']
})
export class ImgashListComponent {
  
  constructor(private apiService: DefaultapiserviceService) {}
  
  imgHashData: ImgHashData[];
  imgHashDataSubscriptionTi: Subscription;
  selectedImage: any;
  selectedImageId: string;

  ngOnInit() {
    this.imgHashDataSubscriptionTi = interval(5000).pipe(
      startWith(0),
      switchMap(() => this.apiService.getImgHashDataList())
    ).subscribe( result => {
      console.log("Handling result (getImgHashDataList)...", result);
      this.imgHashData = result.values;
    } )

  }

  ngOnDestroy() {
    this.imgHashDataSubscriptionTi.unsubscribe();
  }

  fetchImage(imageId) {
    this.selectedImage = null;
    this.selectedImageId = null;
    console.log("Fetching image... ", imageId);
    this.apiService.getSmallImage(imageId).subscribe(result => {
      console.log("Fetched image: ", result);
      this.selectedImage = result;
      this.selectedImageId = imageId;
    });
  }

  
  transformImage(): void {
    var arg = new ImageTransformInstruction();
    arg.imageId = this.selectedImageId;
    arg.imageWidth = 128;
    arg.imageHeight = 128;
    console.log("transformImage... ", arg);
    this.apiService.imageTransform(arg)
    .subscribe(scanEnqueueReceipt => {
      console.log("Result: ", scanEnqueueReceipt)
    });  
  }
}
