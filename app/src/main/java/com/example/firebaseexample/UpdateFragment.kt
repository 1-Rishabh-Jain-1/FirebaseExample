package com.example.firebaseexample

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.firebaseexample.Models.Contacts
import com.example.firebaseexample.databinding.FragmentAddBinding
import com.example.firebaseexample.databinding.FragmentUpdateBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class UpdateFragment : Fragment() {
    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!
    private val args: UpdateFragmentArgs by navArgs()
    private lateinit var firebaseRef: DatabaseReference
    private lateinit var storageRef: StorageReference
    private var uri: Uri? = null
    private var imageUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateBinding.inflate(layoutInflater, container, false)
        firebaseRef = FirebaseDatabase.getInstance().getReference("Contacts")
        storageRef = FirebaseStorage.getInstance().getReference("Images")
        val pickImg = registerForActivityResult(ActivityResultContracts.GetContent()){
            binding.imgUpdate.setImageURI(it)
            if(it != null){
                uri = it
            }
        }
        imageUrl = args.imageUrl
        binding.apply {
            editUpdateName.setText(args.name)
            editUpdatePhone.setText(args.phone)
            Picasso.get().load(imageUrl).into(imgUpdate)
            btnUpdate.setOnClickListener {
                updateData()
                findNavController().navigate(R.id.action_updateFragment_to_homeFragment)
            }
            imgUpdate.setOnClickListener {
                context?.let {context ->
                    MaterialAlertDialogBuilder(context)
                        .setTitle("Change Image")
                        .setMessage("Select the option")
                        .setPositiveButton("Change Image") {_,_ ->
                            pickImg.launch("image/*")
                        }
                        .setNegativeButton("Remove Image") {_,_ ->
                            imageUrl = null
                            binding.imgUpdate.setImageResource(R.drawable.img)
                        }
                        .setNeutralButton("Cancel") {_,_ ->

                        }
                        .show()
                    }
                }

        }
        return binding.root
    }

    private fun updateData() {
        val name = binding.editUpdateName.text.toString()
        val phone = binding.editUpdatePhone.text.toString()
        var contacts: Contacts
        if(uri != null) {
            storageRef.child(args.id).putFile(uri!!)
                .addOnSuccessListener { task ->
                    task.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { url ->
                            imageUrl = url.toString()
                            contacts = Contacts(args.id, name, phone, imageUrl)
                            firebaseRef.child(args.id).setValue(contacts)
                                .addOnCompleteListener {
                                    Toast.makeText(context, "Contact Updated", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context,"Error: ${it.message}",Toast.LENGTH_SHORT).show()
                                }
                        }
                }
        }
        if (uri == null) {
            contacts = Contacts(args.id, name, phone, imageUrl)
            firebaseRef.child(args.id).setValue(contacts)
                .addOnCompleteListener {
                    Toast.makeText(context, "Contact Updated", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context,"Error: ${it.message}",Toast.LENGTH_SHORT).show()
                }
        }
        if (imageUrl == null) storageRef.child(args.id).delete()
    }
}