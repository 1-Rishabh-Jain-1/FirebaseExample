package com.example.firebaseexample

import android.os.Bundle
import android.provider.ContactsContract.Data
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.FloatRange
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebaseexample.Adapter.RvContactsAdapter
import com.example.firebaseexample.Models.Contacts
import com.example.firebaseexample.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var contactsList: ArrayList<Contacts>
    private lateinit var firebaseRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addFragment)
        }
        firebaseRef = FirebaseDatabase.getInstance().getReference("Contacts")
        contactsList = arrayListOf()
        fetchData()
        binding.recyclerViewContacts.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this.context)
        }
        return binding.root
    }

    private fun fetchData() {
        firebaseRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                contactsList.clear()
                if(snapshot.exists()){
                    for (contactSnap in snapshot.children){
                        val contact = contactSnap.getValue(Contacts::class.java)
                        contactsList.add(contact!!)
                    }
                }
                val rvAdapter = RvContactsAdapter(contactsList)
                binding.recyclerViewContacts.adapter = rvAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error: ${error}", Toast.LENGTH_SHORT).show()
            }

        })
    }
}