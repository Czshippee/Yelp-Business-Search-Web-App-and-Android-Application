import { Component, OnInit, Input, SimpleChanges } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
declare var bootstrap: any;

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrls: ['./table.component.css']
})
export class TableComponent implements OnInit {

  @Input() search_result:Array<Map<String,String>> = [];
  @Input() bussiness_to_load:any = 0;
  @Input() submit_times:any;
  headers = ["#","Image","Business Name","Rating","Distance(miles)"];
  contents = [{
    num:"",
    image:"",
    id:"",
    name:"",
    rating:"",
    distance:""
  }];
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
  hidden_card_contents = {
    hidden_card_Status:false,
    hidden_card_Category:false,
    hidden_card_Address:false,
    hidden_card_Phonenumber:false,
    hidden_card_Transaction:false,
    hidden_card_Price:false,
    hidden_card_Moreinfo:false,
  }
  twitterLink = "";
  facebookLink = "";
  reviews = []

  mapOption: google.maps.MapOptions = {
    center: { lat: 34.0272126, lng: -118.2729183 },
    zoom : 14
  };
  marker = { lat: 34.0272126, lng: -118.2729183 };
  mapOptions = [{'mapOption':this.mapOption,'marker':this.marker}];

  display = "none";
  hiddentable : boolean = false;
  hiddencard : boolean = true;

  reser : boolean = false;
  calcelreser : boolean = true;
  curentID = 0;

  //reservation data
  curBusinesss = "";
  form: FormGroup = new FormGroup({
    email: new FormControl(''),
    date: new FormControl(''),
    time: new FormControl(''),
  });

  constructor(private formBuilder: FormBuilder) { }

  ngOnInit(): void {
    this.form = this.formBuilder.group(
      {
        email: ['', [Validators.required, Validators.email]],
        date: ['', Validators.required],
        //time: ['', Validators.required],
        hour: ['', Validators.required],
        minute: ['', Validators.required],
      },
    );
  }
  
  ngOnChanges(changes: SimpleChanges) {
    if (changes['search_result']) {
      this.contents = [];
      for (let i=0;i<this.bussiness_to_load; i++){
        var row = {
          num:String(i+1),
          image:changes['search_result'].currentValue[i]['image_url'],
          id:changes['search_result'].currentValue[i]['id'],
          name:changes['search_result'].currentValue[i]['name'],
          rating:changes['search_result'].currentValue[i]['rating'],
          distance:changes['search_result'].currentValue[i]['distance']
        };
        this.contents.push(row);
      }
    }
    if (changes['submit_times']){
      this.hiddentable = false;
      this.hiddencard = true;
    }
  }

  get f(): { [key: string]: AbstractControl } {
    return this.form.controls;
  }

  async show_detail_info(id: any){
    const detailurl = 'https://businessyelpsearch.wl.r.appspot.com/searchdetail';
    var detail_result = await fetch(detailurl+'?&id='+id).then(response => response.json());
    
    //Business Details Tab
    this.card_contents.card_Name = detail_result.Name;
    this.curBusinesss = detail_result.Name
    if (detail_result.Status != null){
      if (detail_result.Status == true){
        this.card_contents.card_openStatus = 'openButton';
        this.card_contents.card_openText = 'Open Now';
      }else{
        this.card_contents.card_openStatus = 'closeButton';
        this.card_contents.card_openText = 'Closed';
      }
    }else{
      this.hidden_card_contents.hidden_card_Status = true;
    }
    if (detail_result.Category == null) this.hidden_card_contents.hidden_card_Category = true;
    else this.card_contents.card_Category = detail_result.Category;
    if (detail_result.Address == null) this.hidden_card_contents.hidden_card_Address = true;
    else this.card_contents.card_Address = detail_result.Address;
    if (detail_result.Phone_number == null) this.hidden_card_contents.hidden_card_Phonenumber = true;
    else this.card_contents.card_Phonenumber = detail_result.Phone_number;
    if ((detail_result.Transaction_Supported == "") && (detail_result.Transaction_Supported == "")){
      this.hidden_card_contents.hidden_card_Transaction = true;
    } else this.card_contents.card_Transaction = detail_result.Transaction_Supported;
    if (detail_result.Price == null) this.hidden_card_contents.hidden_card_Price = true;
    else this.card_contents.card_Price = detail_result.Price;
    if (detail_result.More_info == null) this.hidden_card_contents.hidden_card_Moreinfo = true;
    else this.card_contents.card_Moreinfo = detail_result.More_info;


    this.card_contents.card_Images0 = detail_result.Photos[0];
    this.card_contents.card_Images1 = detail_result.Photos[1];
    this.card_contents.card_Images2 = detail_result.Photos[2];
    this.mapOption.center = { lat: detail_result.Coord.latitude, lng: detail_result.Coord.longitude};
    this.marker = { lat: detail_result.Coord.latitude, lng: detail_result.Coord.longitude};
    this.mapOptions = [{'mapOption':this.mapOption,'marker':this.marker}];
    
    //Map Location Tab
    this.twitterLink = "https://twitter.com/intent/tweet?text=Check%20" + encodeURI(detail_result.Name) + "%20on%20Yelp.&url=" + detail_result.More_info;
    this.facebookLink = "https://www.facebook.com/sharer/sharer.php?u=" + detail_result.More_info + "&quote=Awesome%20Blog!";

    //Reviews Tab
    this.reviews = detail_result.Reviews;

    this.hiddentable = true;
    this.hiddencard = false;
    
  }

  arrow_back(){
    this.hiddentable = false;
    this.hiddencard = true;
  }
  stringifyParams() {
    var urlParams = '?';
    urlParams += '&' + 'busname' + '=' + this.curBusinesss;
    urlParams += '&' + 'email' + '=' + this.form.value.email;
    urlParams += '&' + 'date' + '=' + this.form.value.date;
    urlParams += '&' + 'time' + '=' + this.form.value.time;
    return encodeURI(urlParams);
	}

  async submit_reserv(): Promise<void>{
    if (!this.form.invalid) {
      var storageArray = [];
      var resultID = 0;
      var storageData = localStorage.getItem('reserveData');
      if (storageData!=null){
        // {"data" : [ {1:'q'}, {2:'w'}, {3:'e'} ], "resultID" : 3 }
        resultID = JSON.parse(localStorage.getItem('reserveData') as string)['resultID'];
        storageArray = JSON.parse(localStorage.getItem('reserveData') as string)['data'];
      }
      this.curentID = resultID;
      this.reser = true;
      this.calcelreser = false;
      var userData = {
        "resultID" : resultID,
        "busname" : this.curBusinesss,
        "email": this.form.value.email,
        "date": this.form.value.date,
        "time": this.form.value.hour + ':' + this.form.value.minute,
      };
      storageArray.push(userData);
      resultID ++;
      var toSave = {'resultID':resultID, 'data':storageArray};
      localStorage.setItem("reserveData", JSON.stringify(toSave));
      // const url = 'https://businessyelpsearch.wl.r.appspot.com/reserveForm';
      // var Status = await fetch(url+this.stringifyParams()).then(response => response.json());
      document.getElementById('modelclose')?.click();
      this.form = this.formBuilder.group(
        {
          email: ['', [Validators.required, Validators.email]],
          date: ['', Validators.required],
          hour: ['', Validators.required],
          minute: ['', Validators.required],
        },
      );
      alert('Revervation created!');
    }
  }

  reest_reserv(){
    this.form = this.formBuilder.group(
      {
        email: ['', [Validators.required, Validators.email]],
        date: ['', Validators.required],
        hour: ['', Validators.required],
        minute: ['', Validators.required],
      },
    );
  }

  delete_reservation(){
    var storageData = localStorage.getItem('reserveData');
    var resultID = JSON.parse(storageData as string)['resultID'];
    var storageArray = JSON.parse(storageData as string)['data'];
    
    // delete
    var deleteIndex = -1
    for (let i=0; i<resultID; i++){
      if (storageArray[i]['resultID'] == Number(this.curentID))
      deleteIndex = i;
    }
    if (deleteIndex > -1) {
      storageArray.splice(deleteIndex, 1);
    }
    resultID --;
    
    var toSave = {'resultID':resultID, 'data':storageArray};
    localStorage.setItem("reserveData", JSON.stringify(toSave));
    this.reser = false;
    this.calcelreser = true;
    alert('Revervation cancelled!');
  }

}
