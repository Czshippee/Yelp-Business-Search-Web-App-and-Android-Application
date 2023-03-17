import { Component, OnInit, Input, SimpleChanges } from '@angular/core';


@Component({
  selector: 'app-card',
  templateUrl: './card.component.html',
  styleUrls: ['./card.component.css']
})
export class CardComponent implements OnInit {
 
  @Input() detail_result:Array<Map<String,String>> = [];
  card_contents = {
    card_Name:"",
    card_openStatus:"",
    card_openText:"",
    card_Category:"",
    card_Address:"",
    card_Phonenumber:"",
    card_Transaction:"",
    card_Price:"",
    card_Moreinfo:"",
    card_Images0:"",
    card_Images1:"",
    card_Images2:"",
  };

  mapOptions: google.maps.MapOptions = {
    center: { lat: 38.9987208, lng: -77.2538699 },
    zoom : 14
  }
  marker = {
      position: { lat: 38.9987208, lng: -77.2538699 },
  }
  display = "none";

  constructor() { }

  ngOnInit(): void {
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['detail_result'].currentValue.length!=0) {
      this.card_contents.card_Name = changes['detail_result'].currentValue.Name;
      if (changes['detail_result'].currentValue.Status != null){
        if (changes['detail_result'].currentValue.Status == true){
          this.card_contents.card_openStatus = 'openButton';
          this.card_contents.card_openText = 'Open Now';
        }else{
          this.card_contents.card_openStatus = 'closeButton';
          this.card_contents.card_openText = 'Closed';
        }
      }
      this.card_contents.card_Category = changes['detail_result'].currentValue.Category;
      this.card_contents.card_Address = changes['detail_result'].currentValue.Address;
      this.card_contents.card_Phonenumber = changes['detail_result'].currentValue.Phone_number;
      this.card_contents.card_Transaction = changes['detail_result'].currentValue.Transaction_Supported;
      this.card_contents.card_Price = changes['detail_result'].currentValue.Price;
      this.card_contents.card_Moreinfo = changes['detail_result'].currentValue.More_info;
      this.card_contents.card_Images0 = changes['detail_result'].currentValue.Photos[0];
      this.card_contents.card_Images1 = changes['detail_result'].currentValue.Photos[1];
      this.card_contents.card_Images2 = changes['detail_result'].currentValue.Photos[2];
      
    }
  }

  arrow_back(){}

  reserve_submit(){
    alert('Revervation created!');
  }
}
