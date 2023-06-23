package com.example.firebaseexample

import android.app.Instrumentation.ActivityResult
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.firebaseexample.Models.Contacts
import com.example.firebaseexample.databinding.FragmentAddBinding
import com.example.firebaseexample.databinding.FragmentHomeBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AddFragment : Fragment() {
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var storageRef: StorageReference
    private var uri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        firebaseRef = FirebaseDatabase.getInstance().getReference("Contacts")
        storageRef = FirebaseStorage.getInstance().getReference("Images")
        binding.btnSend.setOnClickListener {
            saveData()
            findNavController().navigate(R.id.action_addFragment_to_homeFragment)
        }
        val pickImg = registerForActivityResult(ActivityResultContracts.GetContent()){
            binding.imgAdd.setImageURI(it)
            if(it != null){
                uri = it
            }
        }
        binding.btnPickImg.setOnClickListener {
            pickImg.launch("image/*")
        }
        return binding.root
    }

    private fun saveData() {
        val name = binding.editName.text.toString()
        val phone = binding.editPhone.text.toString()
        if(name.isEmpty()) {
            binding.editName.error = "Enter a name"
        }
        else if(phone.isEmpty()) {
            binding.editPhone.error = "Enter a phone number"
        }
        else{
            val contactId = firebaseRef.push().key!!
            var contacts: Contacts
            uri?.let {
                storageRef.child(contactId).putFile(it)
                    .addOnSuccessListener {task ->
                        task.metadata!!.reference!!.downloadUrl
                            .addOnSuccessListener {Url ->
                                Toast.makeText(context, "Image Added", Toast.LENGTH_SHORT).show()
                                val imgUrl = Url.toString()
                                contacts = Contacts(contactId, name, phone, imgUrl)
                                firebaseRef.child(contactId).setValue(contacts)
                                    .addOnCompleteListener {
                                        Toast.makeText(context, "Contact Added", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {error ->
                                        Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                    }
            }

    }
}