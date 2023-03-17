import { Component, OnInit, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-booking',
  templateUrl: './booking.component.html',
  styleUrls: ['./booking.component.css']
})
export class BookingComponent implements OnInit {

  resulttable :boolean = true;
  noresulttable:boolean = true;
  headers = ["#","Business Name","Date","Time", "E-mail", ""];
  contents = [{
    resultid:"",
    num:"",
    name:"",
    date:"",
    time:"",
    email:"",
  }];

  constructor() { }

  async ngOnInit(): Promise<void> {
    // const detailurl = 'https://businessyelpsearch.wl.r.appspot.com/seekBookings';
    // var booking_results = await fetch(detailurl).then(response => response.json());
    var storageData = localStorage.getItem('reserveData');
    if ((storageData==null) || (JSON.parse(storageData as string)['resultID'] == 0)){
      this.resulttable = true;
      this.noresulttable = false;
    }else{
      var resultID = JSON.parse(storageData as string)['resultID'];
      var storageArray = JSON.parse(storageData as string)['data'];
      this.contents = [];
      for (let i=0; i<resultID; i++){
        var row = {
          resultid:storageArray[i]['resultID'],
          num:String(i+1),
          name:storageArray[i]['busname'],
          date:storageArray[i]['date'],
          time:storageArray[i]['time'],
          email:storageArray[i]['email'],
        };
        this.contents.push(row);
      }
      this.resulttable = false;
      this.noresulttable = true;
    }
  }

  delete_row(resultid: string){
    var storageData = localStorage.getItem('reserveData');
    var resultID = JSON.parse(storageData as string)['resultID'];
    var storageArray = JSON.parse(storageData as string)['data'];
    
    // delete
    var deleteIndex = -1
    for (let i=0; i<resultID; i++){
      if (storageArray[i]['resultID'] == Number(resultid))
      deleteIndex = i;
    }
    if (deleteIndex > -1) {
      storageArray.splice(deleteIndex, 1);
    }
    resultID --;
    alert('Revervation cancelled!');
    
    // refresh
    if (resultID==0){
      this.resulttable = true;
      this.noresulttable = false;
    }else{
      this.contents = [];
      for (let i=0; i<resultID; i++){
        var row = {
          resultid:storageArray[i]['resultID'],
          num:String(i+1),
          name:storageArray[i]['busname'],
          date:storageArray[i]['date'],
          time:storageArray[i]['time'],
          email:storageArray[i]['email'],
        };
        this.contents.push(row);
      }
      this.resulttable = false;
      this.noresulttable = true;
    }
    var toSave = {'resultID':resultID, 'data':storageArray};
    localStorage.setItem("reserveData", JSON.stringify(toSave));
  }

}
