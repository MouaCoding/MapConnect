package com.example.rhrn.RightHereRightNow;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rhrn.RightHereRightNow.firebase_entry.City;
import com.example.rhrn.RightHereRightNow.firebase_entry.Event;
import com.example.rhrn.RightHereRightNow.firebase_entry.Likes;
import com.example.rhrn.RightHereRightNow.firebase_entry.User;
import com.example.rhrn.RightHereRightNow.util.CircleTransform;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.rhrn.RightHereRightNow.MapsFragment.getBitmapFromURL;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Matt on 4/2/2017.
 */

public class TrendingFragment extends Fragment {
    public static App app = (App) getApplicationContext();
    public Button global, city;
    public ListView trendingList, cityList;
    public EventAdapter eventAdapter;
    public ArrayList<Event> eventList;
    public ArrayList<City> cityArray;
    public CityAdapter cityAdapter;
    public ProgressDialog pd;
    public FloatingActionButton filterCity;
    private static final int FILTER_CITY = 0;
    public int filteredCity = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View r = (View) inflater.inflate(R.layout.trending_posts, container, false);

        filterCity = (FloatingActionButton) r.findViewById(R.id.filter_city);
        filterCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FilterCityActivity.class);
                startActivityForResult(intent, FILTER_CITY);
            }
        });
        global = (Button) r.findViewById(R.id.global_button);
        global.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventList = new ArrayList<>();
                eventAdapter = new EventAdapter(getContext(), eventList);
                trendingList.setAdapter(eventAdapter);
                filterCity.setVisibility(View.GONE);
                queryAllEvents();
            }
        });
        city = (Button) r.findViewById(R.id.city_button);
        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //eventList = new ArrayList<>();
                //eventAdapter = new EventAdapter(getContext(), eventList);
                //trendingList.setAdapter(eventAdapter);
                cityArray = new ArrayList<>();
                filterCity.setVisibility(View.VISIBLE);
                //if(filteredCity == 0)
                //    queryCityEvents();
                //else
                queryFilteredCities();
            }
        });

        eventList = new ArrayList<>();
        cityArray = new ArrayList<>();
        trendingList = (ListView) r.findViewById(R.id.global_list_trending);
        cityList = (ListView) r.findViewById(R.id.global_list_trending);

        queryAllEvents();
        return r;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILTER_CITY && resultCode == Activity.RESULT_OK) {

            filteredCity = 1;
            Toast.makeText(getContext(), "City Settings Changed.", Toast.LENGTH_SHORT).show();

            cityArray = new ArrayList<>();
            queryFilteredCities();
            cityAdapter.notifyDataSetChanged();

        }
    }

    public void queryAllEvents() {
        DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("Event").orderByChild("likes").startAt(0).endAt(1000).limitToLast(5).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {
                for (DataSnapshot dataSnapshot : dataSnapshot1.getChildren()) {
                    Event ev = dataSnapshot.getValue(Event.class);
                    eventList.add(0, ev);
                }
                eventAdapter = new EventAdapter(getContext(), eventList);
                trendingList.setAdapter(eventAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static class EventAdapter extends ArrayAdapter<Event> {
        private ImageButton likeButton;
        private ImageButton commentButton;
        private ImageButton shareButton;
        private ImageButton options;

        private int eventDeleted = 0;

        EventAdapter(Context context, ArrayList<Event> users) {
            super(context, R.layout.user_event_framed_layout, R.id.user_event_title, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = super.getView(position, convertView, parent);
            final Event event = getItem(position);
            TextView eventTitle = (TextView) convertView.findViewById(R.id.user_event_title);
            ImageView eventImage = (ImageView) convertView.findViewById(R.id.user_event_mini_image);
            TextView startTime = (TextView) convertView.findViewById(R.id.user_event_start_time);
            TextView endTime = (TextView) convertView.findViewById(R.id.user_event_end_time);
            TextView eventLoc = (TextView) convertView.findViewById(R.id.user_event_location);
            TextView numLikes = (TextView) convertView.findViewById(R.id.user_event_like_count);
            TextView numComments = (TextView) convertView.findViewById(R.id.user_event_comment_count);

            TextView displayNameView = (TextView) convertView.findViewById(R.id.mini_name);
            ImageView profilePicture = (ImageView) convertView.findViewById(R.id.mini_profile_picture);
            TextView userHandleView = (TextView) convertView.findViewById(R.id.mini_user_handle);

            eventTitle.setText(event.eventName);
            startTime.setText(event.startTime);
            endTime.setText(event.endTime);
            eventLoc.setText(event.address);
            numLikes.setText(Integer.toString(event.likes));
            numComments.setText(Integer.toString(event.comments));

            displayNameView.setText(event.DisplayName);
            userHandleView.setText(event.handle);

            setButtons(convertView, event.eventID, event.ownerID);
            if (eventDeleted == 0)
                setExtraValues(event.eventID, event.ownerID);

            try {
                if (event.userProfilePicture != null)
                    Picasso.with(getContext()).load(event.userProfilePicture).transform(new CircleTransform()).into(profilePicture);
                    //profilePicture.setImageBitmap(getBitmapFromURL(event.userProfilePicture));
                else
                    Picasso.with(getContext()).load(R.mipmap.ic_launcher).transform(new CircleTransform()).into(profilePicture);
                //profilePicture.setImageResource(R.mipmap.ic_launcher);
            } catch (Exception e) {
            }
            try {
                if (event.ProfilePicture != null)
                    Picasso.with(getContext()).load(event.userProfilePicture).into(eventImage);
                    //eventImage.setImageBitmap(getBitmapFromURL(event.ProfilePicture));
                else
                    Picasso.with(getContext()).load(R.drawable.images).into(eventImage);
                //eventImage.setImageResource(R.drawable.ic_group_black_24dp);
            } catch (Exception e) {
            }

            //On clicks to navigate to view user or event
            displayNameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ViewUserActivity.class);
                    intent.putExtra("otherUserID", event.ownerID);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);
                }
            });
            profilePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ViewUserActivity.class);
                    intent.putExtra("otherUserID", event.ownerID);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);
                }
            });
            eventTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ViewEventActivity.class);
                    intent.putExtra("eventid", event.eventID);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);
                }
            });
            eventImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), ViewEventActivity.class);
                    intent.putExtra("eventid", event.eventID);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);
                }
            });
            return convertView;
        }

        public void setButtons(final View view, final String EventID, final String currUsr) {
            likeButton = (ImageButton) view.findViewById(R.id.user_event_like_button);
            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Likes.hasLiked(2, EventID, currUsr)) {
                        likeButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.colorTextDark));
                        Toast.makeText(getContext(), "Unliked", Toast.LENGTH_SHORT).show();
                        FirebaseDatabase.getInstance().getReference("Likes").child(EventID).child(currUsr).removeValue();
                        Event.changeCount("likes", EventID, false);
                    } else {
                        likeButton.setColorFilter(ContextCompat.getColor(getContext(), R.color.crimson));
                        Likes.Like(2, EventID, currUsr);
                        Event.changeCount("likes", EventID, true);
                        Toast.makeText(getContext(), "Liked", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            commentButton = (ImageButton) view.findViewById(R.id.user_event_comment_button);
            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = getContext();
                    Bundle params = new Bundle();
                    Intent intent = new Intent(context, CommentsListActivity.class);
                    intent.putExtra("postID", EventID.toString());
                    context.startActivity(intent);

                }
            });

            shareButton = (ImageButton) view.findViewById(R.id.user_event_share_button);
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: increment shares, implement sharing
                }
            });
            options = (ImageButton) view.findViewById(R.id.mini_profile_more_button);
            options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMenu(view, currUsr, EventID);
                }
            });

        }

        public void setExtraValues(final String eventID, final String ownerID) {
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref.child("User").child(ownerID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User owner = dataSnapshot.getValue(User.class);
                    ref.child("Event").child(eventID).child("DisplayName").setValue(owner.DisplayName);
                    ref.child("Event").child(eventID).child("handle").setValue(owner.handle);
                    try {
                        ref.child("Event").child(eventID).child("userProfilePicture").setValue(owner.ProfilePicture);
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }


        public void popupMenu(View view, final String ownerID, final String eventID) {
            options = (ImageButton) view.findViewById(R.id.mini_profile_more_button);
            final PopupMenu popup = new PopupMenu(view.getContext(), options);
            popup.getMenuInflater().inflate(R.menu.event_options, popup.getMenu());
            if (String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getUid()).equals(ownerID))
                popup.getMenu().findItem(R.id.delete_event).setVisible(true);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    int i = item.getItemId();
                    if (i == R.id.delete_event) {
                        promptDelete(ownerID, eventID);
                        return true;
                    }
                    if (i == R.id.report_event) {
                        Toast.makeText(getApplicationContext(), "Reporting Event...", Toast.LENGTH_SHORT).show();
                        reportEvent(ownerID, eventID);
                        return true;
                    } else {
                        return onMenuItemClick(item);
                    }
                }
            });
            popup.show();
        }

        public void promptDelete(final String ownerID, final String eventID) {
            android.support.v7.app.AlertDialog.Builder dlgAlert = new android.support.v7.app.AlertDialog.Builder(getContext());
            dlgAlert.setMessage("Are you sure you want to delete this event? This action cannot be undone!");
            dlgAlert.setTitle("Delete Event?");

            dlgAlert.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    eventDeleted = 1;
                    //Perform delete
                    Toast.makeText(getContext(), "Deleting Event...", Toast.LENGTH_SHORT).show();
                    FirebaseDatabase.getInstance().getReference().child("Event").child(eventID).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("EventLocations").child(eventID).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("OtherEventLocations").child(eventID).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("PartyEventLocations").child(eventID).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("SportEventLocations").child(eventID).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("EducationEventLocations").child(eventID).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("ClubEventEventLocations").child(eventID).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(eventID).removeValue();


                    Toast.makeText(getContext(), "Event Deleted!", Toast.LENGTH_SHORT).show();
                    //TODO: update likes received...
                }
            });

            //if user cancels
            dlgAlert.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            });

            dlgAlert.setCancelable(true);
            dlgAlert.create();
            dlgAlert.show();
        }

        public void reportEvent(final String ownerID, final String eventID) {
            final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Event");
            ref.child(eventID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if ((String) dataSnapshot.child("description").getValue() == null) return;
                    else {
                        if (!dataSnapshot.child("numberOfReports").exists())
                            ref.child(eventID).child("numberOfReports").setValue(0);
                        else {
                            long numberOfReports = (long) dataSnapshot.child("numberOfReports").getValue();
                            //parse whitespace
                            String[] content = ((String) dataSnapshot.child("description").getValue()).split("\\s+");
                            if (hasBadWord(content)) {
                                numberOfReports++;
                                ref.child(eventID).child("numberOfReports").setValue(numberOfReports);
                                //TODO: set the amount of reports before a event is deleted
                                if (numberOfReports > 5) {
                                    FirebaseDatabase.getInstance().getReference().child("Event").child(eventID).removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("EventLocations").child(eventID).removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("OtherEventLocations").child(eventID).removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("PartyEventLocations").child(eventID).removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("SportEventLocations").child(eventID).removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("EducationEventLocations").child(eventID).removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("ClubEventEventLocations").child(eventID).removeValue();
                                    FirebaseDatabase.getInstance().getReference().child("Likes").child(eventID).removeValue();
                                }
                            } //Has bad word
                        }//else number of reports exists
                    }//else event has content
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        public boolean hasBadWord(String[] content) {
            int i = 0;
            for (String badWord : app.badWords) {
                content[i] = content[i].toLowerCase();
                if (content[i].contains(badWord)) {
                    Toast.makeText(getContext(), "Event has been reported.", Toast.LENGTH_SHORT).show();
                    return true;
                }
                i++;
            }
            Toast.makeText(getContext(), "There is nothing to report.", Toast.LENGTH_SHORT).show();
            return false;
        }


    }


    public class CityAdapter extends ArrayAdapter<City> {
        CityAdapter(Context context, ArrayList<City> cities) {
            super(context, R.layout.city_layout, R.id.city_name, cities);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = super.getView(position, convertView, parent);
            final City city = getItem(position);
            TextView cityName = (TextView) convertView.findViewById(R.id.city_name);
            ImageView cityImage = (ImageView) convertView.findViewById(R.id.city_image);
            TextView cityLocation = (TextView) convertView.findViewById(R.id.city_location);

            cityName.setText(city.CityName);
            cityLocation.setText(city.CityName + ", " + city.State + ", " + city.Country);

            cityName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), CityEventsActivity.class);
                    intent.putExtra("CityName", city.CityName);
                    startActivity(intent);
                }
            });
            cityImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), CityEventsActivity.class);
                    intent.putExtra("CityName", city.CityName);
                    startActivity(intent);
                }
            });
            try {
                if (city.Picture != null)
                    Picasso.with(getContext()).load(city.Picture).into(cityImage);
                else
                    Picasso.with(getContext()).load(R.drawable.cityscape).into(cityImage);
            } catch (Exception e) {
            }
            return convertView;
        }
    }

    public void queryCityEvents() {
        DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("City").orderByChild("CityName").startAt("A").endAt("Z").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {
                for (DataSnapshot dataSnapshot : dataSnapshot1.getChildren()) {
                    City cty = dataSnapshot.getValue(City.class);
                    cityArray.add(cty);
                }
                cityAdapter = new CityAdapter(getContext(), cityArray);
                cityList.setAdapter(cityAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void queryFilteredCities() {
        DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference().child("CityFilters");
        RootRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderByChild("CityName").startAt("A").endAt("Z").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot1) {
                for (DataSnapshot dataSnapshot : dataSnapshot1.getChildren()) {
                    City cty = dataSnapshot.getValue(City.class);
                    cityArray.add(cty);
                }
                cityAdapter = new CityAdapter(getContext(), cityArray);
                cityList.setAdapter(cityAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void showProgressDialog() {
        pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading...");
        pd.show();
    }
}
